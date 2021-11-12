package io.mongock.driver.dynamodb.repository

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.ConditionalOperator
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue
import io.mongock.driver.api.entry.ChangeEntry
import io.mongock.driver.core.lock.LockEntry
import io.mongock.driver.core.lock.LockEntry.KEY_FIELD
import io.mongock.driver.core.lock.LockEntry.OWNER_FIELD
import io.mongock.driver.core.lock.LockEntry.STATUS_FIELD
import io.mongock.driver.core.lock.LockPersistenceException
import io.mongock.driver.core.lock.LockRepository
import io.mongock.driver.core.lock.LockStatus
import io.mongock.utils.field.Field
import java.util.*
import kotlin.jvm.Throws
import kotlin.math.exp

private val updateConfig = DynamoDBMapperConfig.builder()
    .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.PUT)
    .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
    .build()

class DynamoDBLockRepository(client: AmazonDynamoDBClient, tableName: String, indexCreation: Boolean) :
    LockRepository,
    DynamoDbRepositoryBase(
        client,
        tableName,
        LockEntry::class,
        indexCreation
    ) {

    @Throws(LockPersistenceException::class)
    override fun upsert(newLock: LockEntry) {
        val expression = DynamoDBSaveExpression()
        .withConditionalOperator(ConditionalOperator.AND)
        .withExpectedEntry(KEY_FIELD, ExpectedAttributeValue(true))
        .withExpectedEntry(STATUS_FIELD, ExpectedAttributeValue(AttributeValue((LockStatus.LOCK_HELD.name))))
        .withExpectedEntry(OWNER_FIELD, ExpectedAttributeValue(AttributeValue((newLock.owner))))

       try {
           mapper.save(LockEntryDynamoDB(newLock), expression, updateConfig)
       } catch (ex:Exception) {
           throw LockPersistenceException(expression.toString(), newLock.toString(), ex.message)
       }
    }

    @Throws(LockPersistenceException::class)
    override fun updateIfSameOwner(newLock: LockEntry) {
        val expression = DynamoDBSaveExpression()
            .withConditionalOperator(ConditionalOperator.AND)
            .withExpectedEntry(KEY_FIELD, ExpectedAttributeValue(true))
            .withExpectedEntry(STATUS_FIELD, ExpectedAttributeValue(AttributeValue((LockStatus.LOCK_HELD.name))))
            .withExpectedEntry(OWNER_FIELD, ExpectedAttributeValue(AttributeValue((newLock.owner))))

        try {
            mapper.save(LockEntryDynamoDB(newLock), expression, updateConfig)
        } catch (ex:Exception) {
            throw LockPersistenceException(expression.toString(), newLock.toString(), ex.message)
        }
    }

    override fun findByKey(lockKey: String): LockEntry? {
        return mapper.load(LockEntryDynamoDB::class.java, lockKey)?.lockEntry()
    }

    override fun removeByKeyAndOwner(lockKey: String?, owner: String?) {
        TODO("Not yet implemented")
    }


}

@DynamoDBTable(tableName = "should_not_be_used")
internal class LockEntryDynamoDB private constructor(
    @DynamoDBHashKey(attributeName = LockEntry.KEY_FIELD)
    var key: String?,
    @DynamoDBAttribute(attributeName = LockEntry.STATUS_FIELD)
    var status: String?,
    @DynamoDBAttribute(attributeName = LockEntry.OWNER_FIELD)
    var owner: String?,
    @DynamoDBAttribute(attributeName = LockEntry.EXPIRES_AT_FIELD)
    var expiresAt: Long?
) {

    internal constructor() : this(null, null, null, null)
    internal constructor(lock: LockEntry) : this(lock.key, lock.status, lock.owner, lock.expiresAt.time)

    internal fun mapValues(): Map<String, AttributeValue> {
        val item = HashMap<String, AttributeValue>()
        item[LockEntry.KEY_FIELD] = AttributeValue(key)
        item[LockEntry.STATUS_FIELD] = AttributeValue(status)
        item[LockEntry.OWNER_FIELD] = AttributeValue(owner)
        item[LockEntry.EXPIRES_AT_FIELD] = AttributeValue().withN(expiresAt.toString())
        return item
    }

    internal fun lockEntry(): LockEntry {
        return LockEntry(
            key, status, owner, if (expiresAt != null) Date(expiresAt!!) else null
        )
    }

}