package io.mongock.driver.dynamodb.util

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import io.mongock.driver.dynamodb.driver.*
import io.mongock.utils.Constants
import io.mongock.utils.Constants.*

class TestDynamoDBDriver(
    client: AmazonDynamoDBClient,
    lockAcquiredForMillis: Long,
    lockQuitTryingAfterMillis: Long,
    lockTryFrequencyMillis: Long
) : DynamoDBDriverBase(client, lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis) {

    fun getMigrationRepositoryNameForTest(): String {
        return migrationRepositoryName
    }

    fun getLockRepositoryNameForTest(): String {
        return lockRepositoryName
    }


    companion object {
        @JvmStatic
        fun withDefaultLock(client: AmazonDynamoDBClient): TestDynamoDBDriver {
            return withLockStrategy(
                client = client,
                lockAcquiredForMillis = DEFAULT_LOCK_ACQUIRED_FOR_MILLIS,
                lockQuitTryingAfterMillis = DEFAULT_QUIT_TRYING_AFTER_MILLIS,
                lockTryFrequencyMillis = DEFAULT_TRY_FREQUENCY_MILLIS
            )
        }

        @JvmStatic
        fun withLockStrategy(
            client: AmazonDynamoDBClient,
            lockAcquiredForMillis: Long,
            lockQuitTryingAfterMillis: Long,
            lockTryFrequencyMillis: Long
        ): TestDynamoDBDriver {
            return TestDynamoDBDriver(
                client = client,
                lockAcquiredForMillis = lockAcquiredForMillis,
                lockQuitTryingAfterMillis = lockQuitTryingAfterMillis,
                lockTryFrequencyMillis = lockTryFrequencyMillis
            )
        }
    }
}