package io.mongock.driver.dynamodb.repository

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import com.amazonaws.services.dynamodbv2.model.KeyType
import io.mongock.driver.api.entry.ChangeEntry
import io.mongock.driver.core.lock.LockEntry
import io.mongock.driver.core.lock.LockRepositoryWithEntity
import io.mongock.utils.field.FieldInstance


class DynamoDBLockRepository(client: AmazonDynamoDBClient, tableName: String, indexCreation: Boolean) :
    LockRepositoryWithEntity<Item>,
    DynamoDbRepositoryBase<LockEntry>(
        client,
        tableName,
        listOf(KeySchemaElement(ChangeEntry.KEY_CHANGE_ID, KeyType.HASH)),
        emptyList(),//todo change this
        indexCreation
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