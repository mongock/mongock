package io.mongock.driver.dynamodb.repository

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest
import com.amazonaws.services.dynamodbv2.model.GetItemRequest
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import io.mongock.api.exception.MongockException
import io.mongock.driver.core.lock.LockEntry
import io.mongock.driver.core.lock.LockPersistenceException
import io.mongock.driver.core.lock.LockRepository
import io.mongock.driver.core.lock.LockStatus.LOCK_HELD
import mu.KotlinLogging
import java.util.*


const val KEY_FIELD_DYNAMODB = "lock_key"
const val STATUS_FIELD_DYNAMODB = "lock_status"
const val OWNER_FIELD_DYNAMODB = "lock_owner"
const val EXPIRES_AT_FIELD_DYNAMODB = "expiresAt"

private val logger = KotlinLogging.logger {}

class DynamoDBLockRepository(client: AmazonDynamoDBClient, tableName: String, indexCreation: Boolean,  provisionedThroughput: ProvisionedThroughput?) :
    LockRepository,
    DynamoDbRepositoryBase(
        client,
        tableName,
        LockEntryDynamoDB::class,
        indexCreation,
        provisionedThroughput) {

    @Throws(LockPersistenceException::class)
    override fun insertUpdate(newLock: LockEntry) {
        val expressionAttributeValues = mapOf(
            ":owner_value" to AttributeValue().withS(newLock.owner),
            ":now_value" to AttributeValue().withN(Date().time.toString())
        )
        val expression = "attribute_not_exists($KEY_FIELD_DYNAMODB) " +
                "OR $EXPIRES_AT_FIELD_DYNAMODB < :now_value " +
                "OR $OWNER_FIELD_DYNAMODB = :owner_value"
        performUpdate(newLock, expression, expressionAttributeValues)
    }


    @Throws(LockPersistenceException::class)
    override fun updateIfSameOwner(newLock: LockEntry) {
        val expression = "attribute_exists($KEY_FIELD_DYNAMODB) " +
                "AND $OWNER_FIELD_DYNAMODB = :owner_value " +
                "AND $STATUS_FIELD_DYNAMODB = :status_value"
        val expressionAttributeValues = mapOf(
            ":owner_value" to AttributeValue().withS(newLock.owner),
            ":status_value" to AttributeValue().withS(LOCK_HELD.name)
        )
        performUpdate(newLock, expression, expressionAttributeValues)
    }

    override fun findByKey(lockKey: String): LockEntry? {
        val request = GetItemRequest()
            .withTableName(tableName)
            .withConsistentRead(true)
            .withKey(mapOf(KEY_FIELD_DYNAMODB to AttributeValue().withS(lockKey)))

        val result = client.getItem(request)
        return if (result.item != null) LockEntryDynamoDB(result.item).lockEntry else null
    }

    override fun removeByKeyAndOwner(lockKey: String, owner: String) {
        val request = DeleteItemRequest()
            .withTableName(tableName)
            .withKey(mapOf(KEY_FIELD_DYNAMODB to AttributeValue().withS(lockKey)))
            .withConditionExpression("attribute_exists($KEY_FIELD_DYNAMODB) AND $OWNER_FIELD_DYNAMODB = :owner_value")
            .withExpressionAttributeValues(mapOf(":owner_value" to AttributeValue().withS(owner)))
        try {
            logger.debug { "...trying  to delete lock request: $request" }
            val result = client.deleteItem(request)
            logger.debug { "deletion successfully performed: $result" }
        } catch (ex: ConditionalCheckFailedException) {
            logger.warn { ex.message}
        } catch (ex: Throwable) {
            throw MongockException(ex)
        }
    }


    private fun performUpdate(
        newLock: LockEntry,
        expression: String,
        expressionAttributeValues: Map<String, AttributeValue>
    ) {
        val lockDynamoDB = LockEntryDynamoDB(newLock)

        val request = PutItemRequest()
            .withTableName(tableName)
            .withItem(lockDynamoDB.attributes)
            .withConditionExpression(expression)
            .withExpressionAttributeValues(expressionAttributeValues)
        try {
            logger.debug { "...trying  lock request: $request" }
            val result = client.putItem(request)
            logger.debug { "insertUpdate successfully performed: $result" }
        } catch (ex: ConditionalCheckFailedException) {
            throw LockPersistenceException(request.toString(), lockDynamoDB.toString(), ex.message)
        } catch (ex: Throwable) {
            throw MongockException(ex)
        }
    }

}


@DynamoDBTable(tableName = "should_not_be_used")
internal class LockEntryDynamoDB private constructor(
    @DynamoDBHashKey(attributeName = KEY_FIELD_DYNAMODB)
    var key: String?,
    @DynamoDBAttribute(attributeName = STATUS_FIELD_DYNAMODB)
    var status: String?,
    @DynamoDBAttribute(attributeName = OWNER_FIELD_DYNAMODB)
    var owner: String?,
    @DynamoDBAttribute(attributeName = EXPIRES_AT_FIELD_DYNAMODB)
    var expiresAt: Long?
) {
    internal val attributes: Map<String, AttributeValue>
        get() {
            val item = HashMap<String, AttributeValue>()
            item[KEY_FIELD_DYNAMODB] = AttributeValue(key)
            item[STATUS_FIELD_DYNAMODB] = AttributeValue(status)
            item[OWNER_FIELD_DYNAMODB] = AttributeValue(owner)
            item[EXPIRES_AT_FIELD_DYNAMODB] = AttributeValue().withN(expiresAt.toString())
            return item
        }
    internal val lockEntry: LockEntry
        get() {
            return LockEntry(key, status, owner, Date(expiresAt!!))
        }

    internal constructor() : this(null, null, null, null)
    internal constructor(lock: LockEntry) : this(lock.key, lock.status, lock.owner, lock.expiresAt.time)
    internal constructor(item: Map<String, AttributeValue>) : this(
        item[KEY_FIELD_DYNAMODB]!!.s,
        item[STATUS_FIELD_DYNAMODB]!!.s,
        item[OWNER_FIELD_DYNAMODB]!!.s,
        item[EXPIRES_AT_FIELD_DYNAMODB]!!.n.toLong()
    )


}