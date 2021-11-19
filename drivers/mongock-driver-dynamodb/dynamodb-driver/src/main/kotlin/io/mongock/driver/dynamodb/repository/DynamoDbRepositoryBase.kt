package io.mongock.driver.dynamodb.repository

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.Table
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException
import com.amazonaws.services.dynamodbv2.util.TableUtils
import io.mongock.api.exception.MongockException
import io.mongock.driver.api.common.RepositoryIndexable
import io.mongock.utils.Process
import mu.KotlinLogging
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger {}

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
    private var indexCreation: Boolean,
    private val provisionedThroughput: ProvisionedThroughput?
) : Process, RepositoryIndexable {

    protected val mapper: DynamoDBMapper = DynamoDBMapper(client, mapperConfig(tableName))
    private val dynamoDB: DynamoDB = DynamoDB(client)
    private lateinit var table: Table
    private var ensuredIndex = false

    private val initialize by lazy {
        logger.debug { "initializing [$tableName]" }
        if (!this.ensuredIndex) {
            table = retrieveTable()
            this.ensuredIndex = true
        }
    }

    override fun initialize() {
        this.initialize
    }

    private fun retrieveTable(): Table = try {
        val table = dynamoDB.getTable(tableName)
        table.describe()//just to make it fail if not created
        table
    } catch (ex: ResourceNotFoundException) {
        logger.info { "Table[$tableName] not created" }
        createTable()
    }

    private fun createTable(): Table {
        logger.info { "...creating table[$tableName]" }
        if (!indexCreation) {
            throw MongockException("Table creation not allowed, but not created or wrongly created for table[$tableName]")
        }
        val createTableRequest = mapper.generateCreateTableRequest(mapperClass.java)
        val table = if (provisionedThroughput != null) {
            dynamoDB.createTable(createTableRequest.withProvisionedThroughput(provisionedThroughput))
        } else {
            dynamoDB.createTable(createTableRequest)
        }

        logger.info { "Waiting until[$tableName] is active" }
        TableUtils.waitUntilActive(client, tableName)
        logger.info { "Table[$tableName] created" }
        return table
    }

    override fun setIndexCreation(indexCreation: Boolean) {
        this.indexCreation = indexCreation
    }

}