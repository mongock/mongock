package io.mongock.driver.dynamodb.repository

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.google.gson.Gson
import io.mongock.driver.api.entry.ChangeEntry
import io.mongock.driver.api.entry.ChangeEntry.KEY_AUTHOR
import io.mongock.driver.api.entry.ChangeEntry.KEY_CHANGELOG_CLASS
import io.mongock.driver.api.entry.ChangeEntry.KEY_CHANGESET_METHOD
import io.mongock.driver.api.entry.ChangeEntry.KEY_CHANGE_ID
import io.mongock.driver.api.entry.ChangeEntry.KEY_EXECUTION_HOST_NAME
import io.mongock.driver.api.entry.ChangeEntry.KEY_EXECUTION_ID
import io.mongock.driver.api.entry.ChangeEntry.KEY_EXECUTION_MILLIS
import io.mongock.driver.api.entry.ChangeEntry.KEY_METADATA
import io.mongock.driver.api.entry.ChangeEntry.KEY_STATE
import io.mongock.driver.api.entry.ChangeEntry.KEY_TIMESTAMP
import io.mongock.driver.api.entry.ChangeEntry.KEY_TYPE
import io.mongock.driver.api.entry.ChangeState
import io.mongock.driver.api.entry.ChangeType
import java.util.*
import kotlin.collections.HashMap

private val gson = Gson()

internal const val RANGE_KEY_ID = "${KEY_CHANGE_ID}#${KEY_AUTHOR}"

@DynamoDBTable(tableName = "should_not_be_used")
class ChangeEntryDynamoDB private constructor(
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
    var metadata: String?

) {

    constructor(c: ChangeEntry) : this(
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

    fun changeEntry():ChangeEntry {
        return ChangeEntry(
            executionId,
            changeId,
            author,
            if(timestamp!=null)Date(timestamp!!) else null,
            if(state!=null)ChangeState.valueOf(state!!) else null,
            if(type!=null)ChangeType.valueOf(type!!) else null,
            changeLogClass,
            changeSetMethod,
            executionMillis?:0L,
            executionHostname,
            if(metadata != null) gson.fromJson(metadata, Map::class.java) else {}

        )
    }
    
    fun item():Map<String, AttributeValue> {
        val item = HashMap<String, AttributeValue>()
        item[KEY_CHANGE_ID] = AttributeValue(changeId)
        item[RANGE_KEY_ID] = AttributeValue(rangeKey)
        item[KEY_EXECUTION_ID] = AttributeValue(executionId)
        item[KEY_AUTHOR] = AttributeValue(author)
        item[KEY_TIMESTAMP] = AttributeValue()
        item[KEY_TIMESTAMP]!!.withN(timestamp.toString())
        item[KEY_STATE] = AttributeValue(state)
        item[KEY_TYPE] = AttributeValue(type)
        item[KEY_CHANGELOG_CLASS] = AttributeValue(changeLogClass)
        item[KEY_CHANGESET_METHOD] = AttributeValue(changeSetMethod)
        item[KEY_EXECUTION_MILLIS] = AttributeValue()
        item[KEY_EXECUTION_MILLIS]!!.withN(executionMillis.toString())
        item[KEY_EXECUTION_HOST_NAME] = AttributeValue(executionHostname)
        item[KEY_METADATA] = AttributeValue(metadata)
        
        return item
    }


}