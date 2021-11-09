package io.mongock.driver.dynamodb.repository

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import com.amazonaws.services.dynamodbv2.model.KeyType
import com.amazonaws.services.dynamodbv2.model.TransactWriteItemsRequest
import io.mongock.driver.api.entry.ChangeEntry
import io.mongock.driver.core.entry.ChangeEntryRepositoryWithEntity

open class DynamoDBChangeEntryRepository(client: AmazonDynamoDBClient, tableName: String) :
    ChangeEntryRepositoryWithEntity<Item>,
    DynamoDbRepositoryBase<ChangeEntry>(
        client,
        tableName,
        listOf(KeySchemaElement("change_id", KeyType.HASH), KeySchemaElement("execution_id_author", KeyType.RANGE)),
        emptyList()
    ) {
    var transactionItems:DynamoDBTransactionItems? = null

    override fun setIndexCreation(indexCreation: Boolean) {
        TODO("Not yet implemented")
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