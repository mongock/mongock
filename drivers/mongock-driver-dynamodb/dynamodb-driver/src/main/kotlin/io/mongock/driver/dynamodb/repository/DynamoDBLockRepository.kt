package io.mongock.driver.dynamodb.repository

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import com.amazonaws.services.dynamodbv2.model.KeyType
import io.mongock.driver.core.lock.LockEntry
import io.mongock.driver.core.lock.LockRepositoryWithEntity


class DynamoDBLockRepository(client: AmazonDynamoDBClient, tableName: String) :
    LockRepositoryWithEntity<Item>,
    DynamoDbRepositoryBase<LockEntry>(
        client,
        tableName,
        listOf(KeySchemaElement("change_id", KeyType.HASH)),
        emptyList()//todo change this
    ) {

    override fun setIndexCreation(indexCreation: Boolean) {
        TODO("Not yet implemented")
    }

    override fun insertUpdate(newLock: LockEntry?) {
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