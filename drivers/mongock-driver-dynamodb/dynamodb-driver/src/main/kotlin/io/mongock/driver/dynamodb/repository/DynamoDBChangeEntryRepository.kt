package io.mongock.driver.dynamodb.repository

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.internal.PutItemImpl
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import com.amazonaws.services.dynamodbv2.model.KeyType
import com.amazonaws.services.dynamodbv2.model.Put
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType
import com.amazonaws.services.dynamodbv2.model.TransactWriteItem
import com.amazonaws.services.dynamodbv2.model.TransactWriteItemsRequest
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec
import io.mongock.driver.api.entry.ChangeEntry
import io.mongock.driver.api.entry.ChangeEntry.KEY_AUTHOR
import io.mongock.driver.api.entry.ChangeEntry.KEY_CHANGE_ID
import io.mongock.driver.api.entry.ChangeEntry.KEY_EXECUTION_ID
import io.mongock.driver.core.entry.ChangeEntryRepositoryWithEntity
import io.mongock.utils.field.FieldInstance


private const val KEY_EXECUTION_ID_AUTHOR = "$KEY_EXECUTION_ID#$KEY_AUTHOR"
private val keySchema =
    listOf(KeySchemaElement(KEY_CHANGE_ID, KeyType.HASH), KeySchemaElement(KEY_EXECUTION_ID_AUTHOR, KeyType.RANGE))
private val attributeDefinitions = listOf(
    AttributeDefinition(KEY_CHANGE_ID, ScalarAttributeType.S),
    AttributeDefinition(KEY_EXECUTION_ID_AUTHOR, ScalarAttributeType.S)
)

class DynamoDBChangeEntryRepository(client: AmazonDynamoDBClient, tableName: String, indexCreation: Boolean) :
    ChangeEntryRepositoryWithEntity<Item>,
    DynamoDbRepositoryBase<ChangeEntry>(client, tableName, keySchema, attributeDefinitions, indexCreation) {

    private var _indexCreation = true//todo what happens with this

    var transactionItems: DynamoDBTransactionItems? = null

    override fun setIndexCreation(indexCreation: Boolean) {
        _indexCreation = indexCreation
    }

    override fun isAlreadyExecuted(changeSetId: String?, author: String?): Boolean {
        TODO("THIS WILL SOON BE DELETED. It shouldn't be used")
    }

    override fun getEntriesLog(): List<ChangeEntry> {
        return mapper.scan(ChangeEntryDynamoDB::class.java, DynamoDBScanExpression())
            .map { it as ChangeEntry }
            .toList()
    }

    override fun saveOrUpdate(changeEntry: ChangeEntry) {
        val changeEntryDynamoDB = ChangeEntryDynamoDB(changeEntry)
        if (transactionItems != null) {
            val toEntity = toEntity(changeEntry)
            TODO("NOT IMPLEMENTED YET")
        } else {
            mapper.save(changeEntryDynamoDB)
        }
    }

    override fun save(changeEntry: ChangeEntry?) {
        TODO("Not yet implemented")
    }

    fun cleanTransactionRequest() {
        transactionItems = null
    }


}