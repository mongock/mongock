package io.mongock.driver.dynamodb.repository

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.spec.UpdateTableSpec
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import com.amazonaws.services.dynamodbv2.model.KeyType
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException
import com.amazonaws.services.dynamodbv2.model.UpdateTableRequest
import com.amazonaws.services.dynamodbv2.util.TableUtils
import com.google.gson.Gson
import io.mongock.api.exception.MongockException
import io.mongock.driver.api.common.EntityRepository
import io.mongock.driver.api.entry.ChangeEntry
import io.mongock.driver.api.entry.ChangeState
import io.mongock.driver.api.entry.ChangeType
import io.mongock.utils.field.FieldInstance
import mu.KotlinLogging
import java.util.*


private val logger = KotlinLogging.logger {}

private const val INDEX_ENSURE_MAX_TRIES = 2

private const val RANGE_KEY_SEPARATOR = "#"
private const val RANGE_VALUE_SEPARATOR = "#"
private val GSON = Gson()

private enum class TableState { OK, NOT_FOUND, WRONG_INDEX }

abstract class DynamoDbRepositoryBase<DOMAIN_CLASS>(
    private val client: AmazonDynamoDBClient,
    private val tableName: String,
    private val keySchemaElements: List<KeySchemaElement>,//  In order: hash key followed by an optional range key
    private val attributeDefinitions: List<AttributeDefinition>,
    private val indexCreation: Boolean
) : EntityRepository<DOMAIN_CLASS, Item> {
    protected val dynamoDB: DynamoDB = DynamoDB(client)
    private var ensuredIndex = false

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
        if (tableState != TableState.OK) {
            logger.debug { "Table not OK: $tableState" }
            when (tableState) {
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
            if (currentKeySchema.size != keySchemaElements.size) {
                return TableState.WRONG_INDEX
            }
            for (key in keySchemaElements) {
                if (!currentKeySchema.contains(key)) {
                    logger.warn { "${key.attributeName} not found in $tableName" }
                    return TableState.WRONG_INDEX
                }
            }
        } catch (ex: ResourceNotFoundException) {
            logger.info { "Table $tableName not created" }
            return TableState.NOT_FOUND
        }

        logger.debug { "Table $tableName created" }
        return TableState.OK
    }

    override fun mapFieldInstances(fieldInstanceList: MutableList<FieldInstance>): Item {

        val hashKey = keySchemaElements.first { key -> key.keyType == KeyType.HASH.name }
        val rangeKey = keySchemaElements.firstOrNull { key -> key.keyType == KeyType.RANGE.name }

        val fieldsMap= fieldInstanceList.associate { it.name to it.value }
        val hashKeyValue = fieldsMap[hashKey.attributeName]
        var rangeKeySplit:List<String> = listOf()

        val item = if (rangeKey != null) {
            rangeKeySplit = rangeKey.attributeName.split(RANGE_KEY_SEPARATOR)
                .map { fieldsMap[it].toString() }
                .toList()
            val rangeKeyValue = rangeKeySplit .joinToString(separator = RANGE_VALUE_SEPARATOR)
            Item().withPrimaryKey(hashKey.attributeName, hashKeyValue, rangeKey.attributeName, rangeKeyValue)
        } else {
            Item().withPrimaryKey(hashKey.attributeName, hashKeyValue)
        }
        fieldInstanceList
            .filter {  it.name != hashKey.attributeName && !rangeKeySplit.contains(it.name)}
            .forEach{ setValue(item, it)}
        return item

    }

    private fun setValue(item: Item, field: FieldInstance) {
        if(field.value == null) {
            item.withString(field.name, null)
        }
        when(field.value) {
            is String -> item.withString(field.name, field.value as String)
            is Date -> item.withLong(field.name, (field.value as Date).time)
            is ChangeState -> item.withString(field.name, (field.value as ChangeState).name)
            is ChangeType -> item.withString(field.name, (field.value as ChangeType).name)
            is Long -> item.withLong(field.name, (field.value as Long))
            is Any -> item.withJSON(field.name, GSON.toJson(field.value))
        }
    }


}