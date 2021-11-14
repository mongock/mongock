package io.mongock.driver.dynamodb.repository

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.Table
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException
import com.amazonaws.services.dynamodbv2.util.TableUtils
import com.google.gson.Gson
import io.mongock.api.exception.MongockException
import io.mongock.driver.api.common.RepositoryIndexable
import io.mongock.utils.Process
import mu.KotlinLogging
import kotlin.reflect.KClass


private val logger = KotlinLogging.logger {}

private const val INDEX_TABLE_MAX_TRIES = 2

private enum class TableState { OK, NOT_FOUND, WRONG_INDEX }

private fun mapperConfig(tableName: String) = DynamoDBMapperConfig
    .builder()
    .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
    .withPaginationLoadingStrategy(DynamoDBMapperConfig.PaginationLoadingStrategy.EAGER_LOADING)
    .withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(tableName))
    .build()

abstract class DynamoDbRepositoryBase(
    protected val client: AmazonDynamoDBClient,
    protected val tableName: String,
    private val mapperClass: KClass<*>,
    private var indexCreation: Boolean
) : Process, RepositoryIndexable {

    protected val mapper: DynamoDBMapper = DynamoDBMapper(client, mapperConfig(tableName))
    private val dynamoDB: DynamoDB = DynamoDB(client)
    protected lateinit var table:Table
    private var ensuredIndex = false


    @Synchronized
    override fun initialize() {
        logger.debug { "initializing [$tableName]" }
        if (!this.ensuredIndex) {
            ensureTable(INDEX_TABLE_MAX_TRIES)
            this.ensuredIndex = true
        }
    }


    private fun ensureTable(tryCounter: Int) {
        logger.debug { "ensuring table[$tableName] is created and correct" }
        if (tryCounter <= 0) {
            throw MongockException("Max tries $INDEX_TABLE_MAX_TRIES index  creation")
        }
        dynamoDB.listTables()
        table = dynamoDB.getTable(tableName)
        val tableState = getTableState(table)
        if (tableState != TableState.OK) {
            logger.debug { "Table[$tableName] not OK $tableState" }
            when (tableState) {
                TableState.NOT_FOUND -> table = createTable()
                TableState.WRONG_INDEX -> fixIndexTable()
            }
            ensureTable(tryCounter - 1)
        }
    }

    private fun fixIndexTable() {
        logger.info { "...fixing indexes at table[$tableName]" }
        if (!indexCreation) {
            throw MongockException("Index creation not allowed, but not created or wrongly created for table[$tableName]")
        }
        throw MongockException("Key schema wrong in table[$tableName]. Mongock doesn't provide hot fix for this at the moment")
    }


    private fun createTable(): Table {
        logger.info { "...creating table[$tableName]" }
        if (!indexCreation) {
            throw MongockException("Table creation not allowed, but not created or wrongly created for table[$tableName]")
        }
        val table = dynamoDB.createTable(
            mapper.generateCreateTableRequest(mapperClass.java)
                .withProvisionedThroughput(ProvisionedThroughput(1L, 1L))
        )
        logger.info { "Waiting until[$tableName] is active" }
        TableUtils.waitUntilActive(client, tableName)
        logger.info { "Table[$tableName] created" }
        return table
    }


    private fun getTableState(table: Table): TableState {

        try {
            val description = table.describe()
            //TODO do the following by checking the mapper class(reflection)
//            val currentKeySchema = description.keySchema
//            if (currentKeySchema.size != keySchemaElements.size) {
//                return TableState.WRONG_INDEX
//            }
//            for (key in keySchemaElements) {
//                if (!currentKeySchema.contains(key)) {
//                    logger.warn { "${key.attributeName} not found in table[$tableName]" }
//                    return TableState.WRONG_INDEX
//                }
//            }
        } catch (ex: ResourceNotFoundException) {
            logger.info { "Table[$tableName] not created" }
            return TableState.NOT_FOUND
        }

        logger.debug { "Table[$tableName] created" }
        return TableState.OK
    }

    override fun setIndexCreation(indexCreation: Boolean) {
        this.indexCreation = indexCreation
    }

}