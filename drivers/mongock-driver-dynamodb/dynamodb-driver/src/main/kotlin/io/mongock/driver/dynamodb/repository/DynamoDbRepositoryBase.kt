package io.mongock.driver.dynamodb.repository

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.spec.UpdateTableSpec
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException
import com.amazonaws.services.dynamodbv2.model.UpdateTableRequest
import com.amazonaws.services.dynamodbv2.util.TableUtils
import io.mongock.api.exception.MongockException
import io.mongock.driver.api.common.EntityRepository
import mu.KotlinLogging


private val logger = KotlinLogging.logger {}

private const val INDEX_ENSURE_MAX_TRIES = 2

private enum class TableState { OK, NOT_FOUND, WRONG_INDEX }

abstract class DynamoDbRepositoryBase<DOMAIN_CLASS>(
    private val client: AmazonDynamoDBClient,
    private val tableName: String,
    private val keySchemaElements: List<KeySchemaElement>,
    private val attributeDefinitions: List<AttributeDefinition>
) : EntityRepository<DOMAIN_CLASS, Item> {
    protected val dynamoDB: DynamoDB = DynamoDB(client)
    private var ensuredIndex = false
    var indexCreation: Boolean = true

    @Synchronized
    override fun initialize() {
        logger.debug { "initializing $tableName" }
        if (!this.ensuredIndex) {
            ensureIndex(INDEX_ENSURE_MAX_TRIES)
            this.ensuredIndex = true
        }
    }

    private fun ensureIndex(tryCounter: Int) {
        logger.debug { "ensuring index at table $tableName" }
        if (tryCounter <= 0) {
            throw MongockException("Max tries $INDEX_ENSURE_MAX_TRIES index  creation")
        }
        val tableState = getTableState()
        if(tableState != TableState.OK) {
            logger.debug { "Table not OK: $tableState" }
            when(tableState) {
                TableState.NOT_FOUND -> createTable()
                TableState.WRONG_INDEX -> fixIndexTable()
            }
            ensureIndex(tryCounter - 1)
        }
    }

    private fun fixIndexTable() {
        logger.info { "...fixing indexes at table $tableName" }
        if (!indexCreation) {
            throw MongockException("Index creation not allowed, but not created or wrongly created for table $tableName")
        }
        throw MongockException("Key schema wrong in table $tableName. Mongock doesn't provide hot fix for this at the moment")
    }


    private fun createTable() {
        logger.info { "...creating table $tableName" }
        if (!indexCreation) {
            throw MongockException("Table creation not allowed, but not created or wrongly created for table $tableName")
        }
        val table = dynamoDB.createTable(
            CreateTableRequest()
                .withTableName(tableName)
                .withKeySchema(keySchemaElements)
                .withAttributeDefinitions(attributeDefinitions)
                .withProvisionedThroughput(ProvisionedThroughput(1L, 1L))
        )
        logger.info { "Waiting until $tableName is active" }
        TableUtils.waitUntilActive(client, tableName)
        logger.info { "Table $tableName created" }
    }


    private fun getTableState(): TableState {
        dynamoDB.listTables()
        val table = dynamoDB.getTable(tableName);
        try {
            val description = table.describe()
            val currentKeySchema = description.keySchema
            if(currentKeySchema.size != keySchemaElements.size) {
                return TableState.WRONG_INDEX
            }
            for(key in keySchemaElements) {
                if(!currentKeySchema.contains(key)) {
                    logger.warn { "${key.attributeName} not found in $tableName" }
                    return TableState.WRONG_INDEX
                }
            }
        } catch (ex: ResourceNotFoundException ) {
            logger.info { "Table $tableName not created" }
            return TableState.NOT_FOUND
        }

        logger.debug { "Table $tableName created" }
        return TableState.OK
    }

}