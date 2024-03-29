package io.mongock.driver.dynamodb.repository

import com.amazonaws.services.dynamodbv2.model.TransactWriteItem
import io.mongock.api.exception.MongockException

open class DynamoDBTransactionItems {
    private val max = 24
    private var changeEntryAdded: Boolean = false
    internal val items = ArrayList<TransactWriteItem>()

    fun add(item:TransactWriteItem) {
        if(items.size >= max) {
            throw MongockException("exceeded maximum number of items: $max")
        }
        items.add(item)
    }

    internal fun addChangeEntry(item:TransactWriteItem) {
        items.add(item)
        changeEntryAdded = true
    }

    /**
     * Returns if there is any transaction item from the user(apart from the changeEntry)
     */
    fun containsUserTransactions(): Boolean = if(changeEntryAdded) items.size > 1 else items.size > 0

    /**
     * Returns if there is any transaction of any kind
     */
    fun containsAnyTransaction(): Boolean = items.size > 0

}
