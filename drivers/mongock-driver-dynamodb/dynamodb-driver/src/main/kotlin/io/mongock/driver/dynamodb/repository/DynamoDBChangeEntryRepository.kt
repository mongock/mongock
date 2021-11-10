package io.mongock.driver.dynamodb.repository

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import com.amazonaws.services.dynamodbv2.model.KeyType
import io.mongock.driver.api.entry.ChangeEntry
import io.mongock.driver.api.entry.ChangeEntry.KEY_AUTHOR
import io.mongock.driver.api.entry.ChangeEntry.KEY_CHANGE_ID
import io.mongock.driver.api.entry.ChangeEntry.KEY_EXECUTION_ID
import io.mongock.driver.core.entry.ChangeEntryRepositoryWithEntity
import io.mongock.utils.field.FieldInstance


private const val KEY_EXECUTION_ID_AUTHOR = "$KEY_EXECUTION_ID#$KEY_AUTHOR"

open class DynamoDBChangeEntryRepository(client: AmazonDynamoDBClient, tableName: String, indexCreation: Boolean) :
    ChangeEntryRepositoryWithEntity<Item>,
    DynamoDbRepositoryBase<ChangeEntry>(
        client,
        tableName,
        listOf(KeySchemaElement(KEY_CHANGE_ID, KeyType.HASH), KeySchemaElement(KEY_EXECUTION_ID_AUTHOR, KeyType.RANGE)),
        emptyList(),//TODO chage this
        indexCreation
    ) {
    var transactionItems: DynamoDBTransactionItems? = null
    private var _indexCreation = true;

    override fun setIndexCreation(indexCreation: Boolean) {
        _indexCreation = indexCreation
    }

    override fun isAlreadyExecuted(changeSetId: String?, author: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getEntriesLog(): MutableList<ChangeEntry> {
        TODO("Not yet implemented")
    }

    override fun saveOrUpdate(changeEntry: ChangeEntry?) {
        TODO("Not yet implemented")
    }

    override fun save(changeEntry: ChangeEntry?) {
        TODO("Not yet implemented")
    }

    fun cleanTransactionRequest() {
        transactionItems = null
    }



}