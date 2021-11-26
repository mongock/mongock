package io.mongock.driver.dynamodb.repository

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.Put
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.amazonaws.services.dynamodbv2.model.TransactWriteItem
import com.google.gson.Gson
import io.mongock.driver.api.entry.ChangeEntry
import io.mongock.driver.api.entry.ChangeEntry.KEY_AUTHOR
import io.mongock.driver.api.entry.ChangeEntry.KEY_CHANGELOG_CLASS
import io.mongock.driver.api.entry.ChangeEntry.KEY_CHANGESET_METHOD
import io.mongock.driver.api.entry.ChangeEntry.KEY_CHANGE_ID
import io.mongock.driver.api.entry.ChangeEntry.KEY_ERROR_TRACE
import io.mongock.driver.api.entry.ChangeEntry.KEY_EXECUTION_HOST_NAME
import io.mongock.driver.api.entry.ChangeEntry.KEY_EXECUTION_ID
import io.mongock.driver.api.entry.ChangeEntry.KEY_EXECUTION_MILLIS
import io.mongock.driver.api.entry.ChangeEntry.KEY_METADATA
import io.mongock.driver.api.entry.ChangeEntry.KEY_STATE
import io.mongock.driver.api.entry.ChangeEntry.KEY_TIMESTAMP
import io.mongock.driver.api.entry.ChangeEntry.KEY_TYPE
import io.mongock.driver.api.entry.ChangeEntryService
import io.mongock.driver.api.entry.ChangeState
import io.mongock.driver.api.entry.ChangeType
import mu.KotlinLogging
import java.util.*


internal const val RANGE_KEY_ID = "${KEY_EXECUTION_ID}#${KEY_AUTHOR}"
private val gson = Gson()
private val logger = KotlinLogging.logger {}

class DynamoDBChangeEntryRepository(
    client: AmazonDynamoDBClient,
    tableName: String,
    indexCreation: Boolean,
    provisionedThroughput: ProvisionedThroughput?
) :
    DynamoDbRepositoryBase(client, tableName, ChangeEntryDynamoDB::class, indexCreation, provisionedThroughput),
    ChangeEntryService {

    var transactionItems: DynamoDBTransactionItems? = null

    override fun isAlreadyExecuted(changeSetId: String?, author: String?): Boolean {
        TODO("THIS WILL SOON BE DELETED. It shouldn't be used")
    }

    override fun getEntriesLog(): List<ChangeEntry> {
        return mapper.scan(
            ChangeEntryDynamoDB::class.java,
            DynamoDBScanExpression(),
            DynamoDBMapperConfig.builder().withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT).build()
        )
            .map { it.changeEntry }
            .toList()
    }

    override fun saveOrUpdate(changeEntry: ChangeEntry) {
        val changeEntryDynamoDB = ChangeEntryDynamoDB(changeEntry)
        if (transactionItems == null) {
            val request = PutItemRequest()
                .withTableName(tableName)
                .withItem(changeEntryDynamoDB.attributes)
            logger.debug("Upserting changeEntry: $request")
            val result = client.putItem(request)
            logger.debug("Upsert performed: $result")
        } else {
            val put = Put().withTableName(tableName).withItem(changeEntryDynamoDB.attributes)
            logger.debug("Added element to transactionItems: $put")
            transactionItems!!.addChangeEntry(TransactWriteItem().withPut(put))
        }
    }

    fun cleanTransactionRequest() {
        transactionItems = null
    }

}

@DynamoDBTable(tableName = "should_not_be_used")
internal class ChangeEntryDynamoDB private constructor(
    @DynamoDBHashKey(attributeName = KEY_CHANGE_ID)
    var changeId: String?,
    @DynamoDBRangeKey(attributeName = RANGE_KEY_ID)
    var rangeKey: String?,
    @DynamoDBAttribute(attributeName = KEY_EXECUTION_ID)
    var executionId: String?,
    @DynamoDBAttribute(attributeName = KEY_AUTHOR)
    var author: String?,
    @DynamoDBAttribute(attributeName = KEY_TIMESTAMP)
    var timestamp: Long?,
    @DynamoDBAttribute(attributeName = KEY_STATE)
    var state: String?,
    @DynamoDBAttribute(attributeName = KEY_TYPE)
    var type: String?,
    @DynamoDBAttribute(attributeName = KEY_CHANGELOG_CLASS)
    var changeLogClass: String?,
    @DynamoDBAttribute(attributeName = KEY_CHANGESET_METHOD)
    var changeSetMethod: String?,
    @DynamoDBAttribute(attributeName = KEY_EXECUTION_MILLIS)
    var executionMillis: Long?,
    @DynamoDBAttribute(attributeName = KEY_EXECUTION_HOST_NAME)
    var executionHostname: String?,
    @DynamoDBAttribute(attributeName = KEY_METADATA)
    var metadata: String?,
    @DynamoDBAttribute(attributeName = KEY_ERROR_TRACE)
    var errorTrace: String?
) {
    internal val item: Item
        get() {
            return Item()
                .withPrimaryKey(KEY_CHANGE_ID, changeId, RANGE_KEY_ID, rangeKey)
                .withString(KEY_CHANGE_ID, changeId)
                .withString(RANGE_KEY_ID, rangeKey)
                .withString(KEY_EXECUTION_ID, executionId)
                .withString(KEY_AUTHOR, author)
                .withNumber(KEY_TIMESTAMP, timestamp)
                .withString(KEY_STATE, state)
                .withString(KEY_TYPE, type)
                .withString(KEY_CHANGELOG_CLASS, changeLogClass)
                .withString(KEY_CHANGESET_METHOD, changeSetMethod)
                .withNumber(KEY_EXECUTION_MILLIS, executionMillis)
                .withString(KEY_EXECUTION_HOST_NAME, executionHostname)
                .withString(KEY_METADATA, metadata)
                .withString(KEY_ERROR_TRACE, errorTrace)
        }

    internal val changeEntry: ChangeEntry
        get() {
            return ChangeEntry(
                executionId,
                changeId,
                author,
                if (timestamp != null) Date(timestamp!!) else null,
                if (state != null) ChangeState.valueOf(state!!) else null,
                if (type != null) ChangeType.valueOf(type!!) else null,
                changeLogClass,
                changeSetMethod,
                executionMillis ?: 0L,
                executionHostname,
                if (metadata != null) gson.fromJson(metadata, Map::class.java) else Unit,
                errorTrace
            )
        }

    internal val attributes: Map<String, AttributeValue>
        get() {
            val attributes = HashMap<String, AttributeValue>()
            attributes[KEY_CHANGE_ID] = AttributeValue(changeId)
            attributes[RANGE_KEY_ID] = AttributeValue(rangeKey)
            attributes[KEY_EXECUTION_ID] = AttributeValue(executionId)
            attributes[KEY_AUTHOR] = AttributeValue(author)
            attributes[KEY_TIMESTAMP] = AttributeValue()
            attributes[KEY_TIMESTAMP]!!.withN(timestamp.toString())
            attributes[KEY_STATE] = AttributeValue(state)
            attributes[KEY_TYPE] = AttributeValue(type)
            attributes[KEY_CHANGELOG_CLASS] = AttributeValue(changeLogClass)
            attributes[KEY_CHANGESET_METHOD] = AttributeValue(changeSetMethod)
            attributes[KEY_EXECUTION_MILLIS] = AttributeValue()
            attributes[KEY_EXECUTION_MILLIS]!!.withN(executionMillis.toString())
            if (executionHostname != null && executionHostname != "") {
                attributes[KEY_EXECUTION_HOST_NAME] = AttributeValue(executionHostname)
            }
            if (metadata != null && metadata != "") {
                attributes[KEY_METADATA] = AttributeValue(metadata)
            }
            if (errorTrace != null && errorTrace != "") {
                attributes[KEY_ERROR_TRACE] = AttributeValue(errorTrace)
            }
            return attributes
        }


    internal constructor(c: ChangeEntry) : this(
        changeId = c.changeId,
        rangeKey = "${c.executionId}#${c.author}",
        executionId = c.executionId,
        author = c.author,
        timestamp = c.timestamp?.time ?: 0L,
        state = (c.state ?: ChangeState.EXECUTED).name,
        type = (c.type ?: ChangeType.EXECUTION).name,
        changeLogClass = c.changeLogClass,
        changeSetMethod = c.changeSetMethod,
        executionMillis = c.executionMillis,
        executionHostname = c.executionHostname ?: "",
        metadata = if (c.metadata != null) gson.toJson(c.metadata) else "",
        errorTrace = c.errorTrace.orElse("")
    )

    constructor() : this(null, null, null, null, null, null, null, null, null, null, null, null, null)
    internal constructor(item: Map<String, AttributeValue>) : this(
        changeId = item[KEY_CHANGE_ID]!!.s,
        rangeKey = item[RANGE_KEY_ID]!!.s,
        executionId = item[KEY_EXECUTION_ID]!!.s,
        author = item[KEY_AUTHOR]!!.s,
        timestamp = item[KEY_TIMESTAMP]!!.n.toLong(),
        state = item[KEY_STATE]!!.s,
        type = item[KEY_TYPE]!!.s,
        changeLogClass = item[KEY_CHANGELOG_CLASS]!!.s,
        changeSetMethod = item[KEY_CHANGESET_METHOD]!!.s,
        executionMillis = item[KEY_EXECUTION_MILLIS]!!.n.toLong(),
        executionHostname = (item[KEY_EXECUTION_HOST_NAME]?.s) ?: "",
        metadata = (item[KEY_METADATA]?.s) ?: "",
        errorTrace = (item[KEY_ERROR_TRACE]?.s) ?: ""
    )

}