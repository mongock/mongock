package io.mongock.driver.dynamodb.util

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.GetItemRequest
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.amazonaws.services.dynamodbv2.model.QueryRequest
import com.amazonaws.services.dynamodbv2.model.Select
import com.amazonaws.services.dynamodbv2.util.TableUtils
import io.kotest.core.spec.style.scopes.DescribeSpecContainerContext
import io.kotest.core.test.TestContext
import io.mongock.driver.api.driver.ConnectionDriver
import io.mongock.driver.api.entry.ChangeEntry
import io.mongock.driver.api.entry.ChangeEntry.KEY_CHANGE_ID
import io.mongock.driver.api.entry.ChangeEntryService
import io.mongock.driver.api.entry.ChangeState
import io.mongock.driver.api.entry.ChangeType
import io.mongock.driver.core.lock.LockEntry
import io.mongock.driver.core.lock.LockRepository
import io.mongock.driver.core.lock.LockStatus
import io.mongock.driver.dynamodb.driver.DynamoDBDriver
import io.mongock.driver.dynamodb.repository.ChangeEntryDynamoDB
import io.mongock.driver.dynamodb.repository.DynamoDBChangeEntryRepository
import io.mongock.driver.dynamodb.repository.DynamoDBLockRepository
import io.mongock.driver.dynamodb.repository.KEY_FIELD_DYNAMODB
import io.mongock.driver.dynamodb.repository.LockEntryDynamoDB
import io.mongock.driver.dynamodb.repository.RANGE_KEY_ID
import org.testcontainers.dynamodb.DynaliteContainer
import org.testcontainers.utility.DockerImageName
import java.util.*


interface TestCompanion<in REPO_EXTRA_CONFIG> {

    fun start()

    fun stopContainer()

    fun checkTableIsCreated(tableName: String)

    fun getDriver(): ConnectionDriver

    /**
     * ChangeEntryService
     */
    fun getChangeService(tableName: String, indexCreation: Boolean, t:REPO_EXTRA_CONFIG): ChangeEntryService

    fun createChangeEntryTable(tableName: String)

    fun insertChangeEntries(tableName: String, vararg entries: ChangeEntry)

    fun createInsert(tableName: String, vararg entries: ChangeEntry)

    fun isInserted(tableName: String, changeEntry: ChangeEntry): Boolean

    fun getChangeEntry(tableName: String, changeEntry: ChangeEntry): ChangeEntry?

    /**
     * LOCK
     */
    fun getLockRepository(tableName: String, indexCreation: Boolean, t:REPO_EXTRA_CONFIG): LockRepository

    fun createLockTable(tableName: String)

    fun isInserted(tableName: String, lockEntry: LockEntry): Boolean

    fun getLockEntry(tableName: String, lockEntry: LockEntry): LockEntry?

    fun insertLockEntries(tableName: String, vararg entries: LockEntry)

    fun createInsert(tableName: String, vararg entries: LockEntry)


}


suspend fun DescribeSpecContainerContext.tearDown(
    name: String,
    test: suspend TestContext.() -> Unit
) {
    it("[tear-down]: $name", test)
}

suspend fun DescribeSpecContainerContext.should(
    name: String,
    test: suspend TestContext.() -> Unit
) {
    it("SHOULD $name", test)
}

suspend fun DescribeSpecContainerContext.xshould(
    name: String,
    test: suspend TestContext.() -> Unit
) {
    xit("SHOULD $name", test)
}

suspend fun DescribeSpecContainerContext.and(
    name: String,
    test: suspend TestContext.() -> Unit
) {
    describe("AND $name", test)
}

suspend fun DescribeSpecContainerContext.and(
    name: String,
    preTest: () -> Unit,
    test: suspend TestContext.() -> Unit
) {
    preTest()
    and(name, test)
}

suspend fun DescribeSpecContainerContext.When(
    name: String,
    test: suspend TestContext.() -> Unit
) {
    describe("WHEN $name", test)
}

suspend fun DescribeSpecContainerContext.xWhen(
    name: String,
    test: suspend TestContext.() -> Unit
) {
    xdescribe("WHEN $name", test)
}

suspend fun DescribeSpecContainerContext.When(
    name: String,
    preTest: () -> Unit,
    test: suspend TestContext.() -> Unit
) {
    preTest()
    When(name, test)
}

suspend fun DescribeSpecContainerContext.xWhen(
    name: String,
    preTest: () -> Unit,
    test: suspend TestContext.() -> Unit
) {
    xWhen(name, test)
}

suspend fun DescribeSpecContainerContext.describe(
    name: String,
    preTest: () -> Unit,
    test: suspend TestContext.() -> Unit
) {
    preTest()
    describe(name, test)
}
