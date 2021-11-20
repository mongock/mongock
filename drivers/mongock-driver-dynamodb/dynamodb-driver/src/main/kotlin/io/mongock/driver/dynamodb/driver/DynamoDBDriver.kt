package io.mongock.driver.dynamodb.driver

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient

class DynamoDBDriver private constructor(
    client: AmazonDynamoDBClient,
    lockAcquiredForMillis: Long,
    lockQuitTryingAfterMillis: Long,
    lockTryFrequencyMillis: Long
) : DynamoDBDriverBase(client, lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis) {

    companion object{
        private const val LOCK_ACQUIRE_FOR_MILLIS = 60 * 1000L
        private const val LOCK_QUIT_TRYING_AFTER_MILLIS = 60 * 1000L
        private const val LOCK_TRY_FREQUENCY_MILLIS = 60 * 1000L

        fun withDefaultLock(client: AmazonDynamoDBClient): DynamoDBDriver {
            return withLockStrategy(
                client = client,
                lockAcquiredForMillis = LOCK_ACQUIRE_FOR_MILLIS,
                lockQuitTryingAfterMillis = LOCK_QUIT_TRYING_AFTER_MILLIS,
                lockTryFrequencyMillis = LOCK_TRY_FREQUENCY_MILLIS
            )
        }

        fun withLockStrategy(client: AmazonDynamoDBClient,
                             lockAcquiredForMillis: Long,
                             lockQuitTryingAfterMillis: Long,
                             lockTryFrequencyMillis: Long):DynamoDBDriver {
            return DynamoDBDriver(
                client = client,
                lockAcquiredForMillis = lockAcquiredForMillis,
                lockQuitTryingAfterMillis = lockQuitTryingAfterMillis,
                lockTryFrequencyMillis = lockTryFrequencyMillis
            )
        }

    }
}