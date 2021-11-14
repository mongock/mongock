package io.mongock.driver.dynamodb.repository

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.model.ConditionalOperator
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue
import com.amazonaws.services.dynamodbv2.model.Put
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.amazonaws.services.dynamodbv2.model.PutRequest
import com.amazonaws.services.dynamodbv2.model.TransactWriteItem
import io.mongock.driver.api.entry.ChangeEntry
import io.mongock.driver.api.entry.ChangeEntryService

import io.mongock.driver.api.entry.ChangeEntry.KEY_CHANGE_ID
class DynamoDBChangeEntryRepository(client: AmazonDynamoDBClient, tableName: String, indexCreation: Boolean) :
    DynamoDbRepositoryBase<ChangeEntry>(client, tableName, ChangeEntryDynamoDB::class, indexCreation),
    ChangeEntryService {

    var transactionItems: DynamoDBTransactionItems? = null

    override fun isAlreadyExecuted(changeSetId: String?, author: String?): Boolean {
        TODO("THIS WILL SOON BE DELETED. It shouldn't be used")
    }

    override fun getEntriesLog(): List<ChangeEntry> {
        return mapper.scan(ChangeEntryDynamoDB::class.java, DynamoDBScanExpression())
            .map { it.changeEntry() }
            .toList()
    }

    override fun upsert(changeEntry: ChangeEntry) {
        val changeEntryDynamoDB = ChangeEntryDynamoDB(changeEntry)
        if (transactionItems != null) {
            val transactionItem: TransactWriteItem = TransactWriteItem()
                .withPut(Put().withTableName(tableName).withItem(changeEntryDynamoDB.item()))
            transactionItems!!.addChangeEntry(transactionItem)
        } else {
            mapper.save(changeEntryDynamoDB)
        }
    }

    fun cleanTransactionRequest() {
        transactionItems = null
    }


}