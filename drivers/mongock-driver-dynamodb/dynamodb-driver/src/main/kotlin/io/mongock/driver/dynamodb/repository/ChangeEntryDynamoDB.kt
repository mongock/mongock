package io.mongock.driver.dynamodb.repository

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import com.google.gson.Gson
import io.mongock.driver.api.entry.ChangeEntry
import io.mongock.driver.api.entry.ChangeState
import io.mongock.driver.api.entry.ChangeType
import java.util.*

private val gson = Gson()

@DynamoDBTable(tableName = "should_not_be_used")
class ChangeEntryDynamoDB(
    executionId: String,
    changeId: String,
    author: String,
    timestamp: Date,
    state: ChangeState,
    type: ChangeType,
    changeLogClass: String,
    changeSetMethod: String,
    executionMillis: Long,
    executionHostname: String,
    metadata: Any
) : ChangeEntry(
    executionId,
    changeId,
    author,
    timestamp,
    state,
    type,
    changeLogClass,
    changeSetMethod,
    executionMillis,
    executionHostname,
    metadata
) {

    constructor(c:ChangeEntry) :this(
        c.executionId,
        c.changeId,
        c.author,
        c.timestamp,
        c.state,
        c.type,
        c.changeLogClass,
        c.changeSetMethod,
        c.executionMillis,
        c.executionHostname,
        c.metadata
    )

    @DynamoDBHashKey(attributeName = KEY_CHANGE_ID)
    override fun getChangeId(): String? {
        return super.getChangeId()
    }

    @DynamoDBRangeKey(attributeName = "${KEY_EXECUTION_ID}#${KEY_AUTHOR}")
    fun getExecutionIdAuthor(): String? {
        return "${super.getExecutionId()}#${super.getAuthor()}}"
    }

//    override fun getExecutionId(): String {
//        return super.getExecutionId()
//    }
//
//    override fun getAuthor(): String? {
//        return super.getAuthor()
//    }

    @DynamoDBAttribute(attributeName = KEY_TIMESTAMP)
    override fun getTimestamp(): Date? {
        return super.getTimestamp()
    }

    @DynamoDBAttribute(attributeName = KEY_STATE)
    fun getStateString(): String {
        return if (state != null) state.name else ChangeState.EXECUTED.name
    }

    @DynamoDBAttribute(attributeName = KEY_CHANGELOG_CLASS)
    override fun getChangeLogClass(): String? {
        return changeLogClass
    }

    @DynamoDBAttribute(attributeName = KEY_CHANGESET_METHOD)
    override fun getChangeSetMethod(): String? {
        return changeSetMethod
    }

    @DynamoDBAttribute(attributeName = KEY_EXECUTION_MILLIS)
    override fun getExecutionMillis(): Long {
        return executionMillis
    }

    @DynamoDBAttribute(attributeName = KEY_EXECUTION_HOST_NAME)
    override fun getExecutionHostname(): String? {
        return executionHostname
    }

    @DynamoDBAttribute(attributeName = KEY_METADATA)
    fun getMetadataString(): Any? {
        return if (metadata != null) gson.toJson(metadata) else ""
    }

    @DynamoDBAttribute(attributeName = KEY_TYPE)
    fun getTypeString(): String {
        return type.name
    }
}