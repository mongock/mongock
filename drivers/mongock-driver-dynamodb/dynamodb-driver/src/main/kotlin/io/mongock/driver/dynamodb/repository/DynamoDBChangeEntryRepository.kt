package io.mongock.driver.dynamodb.repository

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.Put
import com.amazonaws.services.dynamodbv2.model.TransactWriteItem
import com.amazonaws.services.dynamodbv2.model.Update
import com.google.gson.Gson
import io.mongock.driver.api.entry.ChangeEntry
import io.mongock.driver.api.entry.ChangeEntryService
import io.mongock.driver.api.entry.ChangeState
import io.mongock.driver.api.entry.ChangeType
import java.util.*
import kotlin.collections.HashMap


class DynamoDBChangeEntryRepository(client: AmazonDynamoDBClient, tableName: String, indexCreation: Boolean) :
    DynamoDbRepositoryBase(client, tableName, ChangeEntryDynamoDB::class, indexCreation),
    ChangeEntryService {

    var transactionItems: DynamoDBTransactionItems? = null

    override fun isAlreadyExecuted(changeSetId: String?, author: String?): Boolean {
        TODO("THIS WILL SOON BE DELETED. It shouldn't be used")
    }

    override fun getEntriesLog(): List<ChangeEntry> {
        return mapper.scan(ChangeEntryDynamoDB::class.java, DynamoDBScanExpression(), DynamoDBMapperConfig.builder().withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT).build())
            .map { it.changeEntry() }
            .toList()
    }

    override fun upsert(changeEntry: ChangeEntry) {
        val changeEntryDynamoDB = ChangeEntryDynamoDB(changeEntry)

        if (transactionItems == null){
            mapper.save(changeEntryDynamoDB)
        } else {
            //TODO NOT TESTED
            val transactionItem: TransactWriteItem = TransactWriteItem()
            if (!isAlreadyPresent(changeEntryDynamoDB)) {
                println("SHOULD PERFORM INSERT")
                transactionItem.withPut(Put().withTableName(tableName).withItem(changeEntryDynamoDB.mapValues()))
            } else {
                println("SHOULD PERFORM UPDATE")
                transactionItem.withUpdate(Update().withTableName(tableName))
            }
            transactionItems!!.addChangeEntry(transactionItem)
        }
    }

    //TODO if passing the log check just in memory. Challenge, the log should be updated
    private fun isAlreadyPresent(changeEntry: ChangeEntryDynamoDB): Boolean {
        val expression =  DynamoDBQueryExpression<ChangeEntryDynamoDB>()
            .withHashKeyValues(changeEntry)
            .withConsistentRead(true)
        return mapper.query(ChangeEntryDynamoDB::class.java, expression) .any { it.author == changeEntry.author }
    }

    fun cleanTransactionRequest() {
        transactionItems = null
    }


}


private val gson = Gson()

private const val RANGE_KEY_ID = "${ChangeEntry.KEY_EXECUTION_ID}#${ChangeEntry.KEY_AUTHOR}"

@DynamoDBTable(tableName = "should_not_be_used")
internal class ChangeEntryDynamoDB private constructor(
    @DynamoDBHashKey(attributeName = ChangeEntry.KEY_CHANGE_ID)
    var changeId: String?,
    @DynamoDBRangeKey(attributeName = RANGE_KEY_ID)
    var rangeKey: String?,
    @DynamoDBAttribute(attributeName = ChangeEntry.KEY_EXECUTION_ID)
    var executionId: String?,
    @DynamoDBAttribute(attributeName = ChangeEntry.KEY_AUTHOR)
    var author: String?,
    @DynamoDBAttribute(attributeName = ChangeEntry.KEY_TIMESTAMP)
    var timestamp: Long?,
    @DynamoDBAttribute(attributeName = ChangeEntry.KEY_STATE)
    var state: String?,
    @DynamoDBAttribute(attributeName = ChangeEntry.KEY_TYPE)
    var type: String?,
    @DynamoDBAttribute(attributeName = ChangeEntry.KEY_CHANGELOG_CLASS)
    var changeLogClass: String?,
    @DynamoDBAttribute(attributeName = ChangeEntry.KEY_CHANGESET_METHOD)
    var changeSetMethod: String?,
    @DynamoDBAttribute(attributeName = ChangeEntry.KEY_EXECUTION_MILLIS)
    var executionMillis: Long?,
    @DynamoDBAttribute(attributeName = ChangeEntry.KEY_EXECUTION_HOST_NAME)
    var executionHostname: String?,
    @DynamoDBAttribute(attributeName = ChangeEntry.KEY_METADATA)
    var metadata: String?

) {
    internal constructor(c: ChangeEntry) : this(
        changeId = c.changeId,
        rangeKey = "${c.executionId}#${c.author}",
        executionId = c.executionId,
        author = c.author,
        timestamp = c.timestamp?.time?:0L,
        state = (c.state ?: ChangeState.EXECUTED).name,
        type = (c.type ?: ChangeType.EXECUTION).name,
        changeLogClass = c.changeLogClass,
        changeSetMethod = c.changeSetMethod,
        executionMillis = c.executionMillis,
        executionHostname = c.executionHostname?:"",
        metadata = if(c.metadata != null) gson.toJson(c.metadata) else  ""
    )

    constructor():this(null,null,null,null,null,null,null,null,null,null,null,null)

    internal fun changeEntry():ChangeEntry {
        return ChangeEntry(
            executionId,
            changeId,
            author,
            if(timestamp!=null) Date(timestamp!!) else null,
            if(state!=null) ChangeState.valueOf(state!!) else null,
            if(type!=null) ChangeType.valueOf(type!!) else null,
            changeLogClass,
            changeSetMethod,
            executionMillis?:0L,
            executionHostname,
            if(metadata != null) gson.fromJson(metadata, Map::class.java) else {}

        )
    }

    internal fun mapValues():Map<String, AttributeValue> {
        val item = HashMap<String, AttributeValue>()
        item[ChangeEntry.KEY_CHANGE_ID] = AttributeValue(changeId)
        item[RANGE_KEY_ID] = AttributeValue(rangeKey)
        item[ChangeEntry.KEY_EXECUTION_ID] = AttributeValue(executionId)
        item[ChangeEntry.KEY_AUTHOR] = AttributeValue(author)
        item[ChangeEntry.KEY_TIMESTAMP] = AttributeValue()
        item[ChangeEntry.KEY_TIMESTAMP]!!.withN(timestamp.toString())
        item[ChangeEntry.KEY_STATE] = AttributeValue(state)
        item[ChangeEntry.KEY_TYPE] = AttributeValue(type)
        item[ChangeEntry.KEY_CHANGELOG_CLASS] = AttributeValue(changeLogClass)
        item[ChangeEntry.KEY_CHANGESET_METHOD] = AttributeValue(changeSetMethod)
        item[ChangeEntry.KEY_EXECUTION_MILLIS] = AttributeValue()
        item[ChangeEntry.KEY_EXECUTION_MILLIS]!!.withN(executionMillis.toString())
        item[ChangeEntry.KEY_EXECUTION_HOST_NAME] = AttributeValue(executionHostname)
        item[ChangeEntry.KEY_METADATA] = AttributeValue(metadata)

        return item
    }


}