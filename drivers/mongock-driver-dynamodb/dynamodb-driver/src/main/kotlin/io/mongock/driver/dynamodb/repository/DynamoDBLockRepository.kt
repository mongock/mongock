package io.mongock.driver.dynamodb.repository

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import io.mongock.driver.core.lock.LockEntry
import io.mongock.driver.core.lock.LockRepository


class DynamoDBLockRepository(client: AmazonDynamoDBClient, tableName: String, indexCreation: Boolean) :
    LockRepository,
    DynamoDbRepositoryBase<LockEntry>(
        client,
        tableName,
        LockEntry::class,
        indexCreation
    ) {

    override fun upsert(newLock: LockEntry?) {
        TODO("Not yet implemented")
    }

    override fun updateIfSameOwner(newLock: LockEntry?) {
        TODO("Not yet implemented")
    }

    override fun findByKey(lockKey: String?): LockEntry {
        TODO("Not yet implemented")
    }

    override fun removeByKeyAndOwner(lockKey: String?, owner: String?) {
        TODO("Not yet implemented")
    }

    override fun deleteAll() {
        TODO("Not yet implemented")
    }


}