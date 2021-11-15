package io.mongock.driver.dynamodb.repository

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
import org.testcontainers.dynamodb.DynaliteContainer
import org.testcontainers.utility.DockerImageName
import java.util.*


class DynamoDBTestCompanion {

    val transactionServerEnabled = false//todo take this from ENV
    private var container: DynaliteContainer? = null
    private var client: AmazonDynamoDBClient? = null
    private var dynamoDB: DynamoDB? = null
    fun start() {
        if (transactionServerEnabled) {
            client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(
                    AwsClientBuilder.EndpointConfiguration(
                        "dynamodb.eu-west-1.amazonaws.com",
                        "eu-west-1"
                    )
                )
                .withCredentials(
                    AWSStaticCredentialsProvider(
                        BasicAWSCredentials(
                            "ACCESS_KEY",//todo take this from ENV
                            "SECRET_KEY"
                        )
                    )
                )
                .build() as AmazonDynamoDBClient

        } else {
            container = DynaliteContainer(DockerImageName.parse("quay.io/testcontainers/dynalite").withTag("v1.2.1-1"))
            container!!.start()
            client = container!!.client as AmazonDynamoDBClient
        }


        dynamoDB = DynamoDB(client)

    }

    fun stopContainer() {
        if (container != null && container!!.isRunning) {
            container!!.stop()
        }
    }

    fun getChangeService(tableName: String, indexCreation: Boolean): ChangeEntryService {
        return DynamoDBChangeEntryRepository(client!!, tableName, indexCreation)
    }

    fun checkTableIsCreated(tableName: String) {
        dynamoDB!!.getTable(tableName).describe()
    }

    fun createChangeEntryTable(tableName: String) {
        val mapperConfig = DynamoDBMapperConfig
            .builder()
            .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
            .withPaginationLoadingStrategy(DynamoDBMapperConfig.PaginationLoadingStrategy.EAGER_LOADING)
            .withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(tableName))
            .build()
        TableUtils.createTableIfNotExists(
            client,
            DynamoDBMapper(client, mapperConfig).generateCreateTableRequest(ChangeEntryDynamoDB::class.java)
                .withProvisionedThroughput(ProvisionedThroughput(1L, 1L))
        )
        TableUtils.waitUntilActive(client, tableName)
    }


    fun insertChangeEntries(tableName: String, vararg entries: ChangeEntry) {
        entries.forEach {
            client!!.putItem(PutItemRequest().withTableName(tableName).withItem(ChangeEntryDynamoDB(it).attributes))
        }
    }

    fun createInsert(tableName: String, vararg entries: ChangeEntry) {
        createChangeEntryTable(tableName)
        insertChangeEntries(tableName, *entries)
    }

    fun isInserted(tableName: String, changeEntry: ChangeEntry): Boolean {
        return getChangeEntry(tableName, changeEntry) != null
    }

    fun getChangeEntry(tableName: String, changeEntry: ChangeEntry): ChangeEntry? {
        val dynamoEntry = ChangeEntryDynamoDB(changeEntry)
        val request = GetItemRequest()
            .withTableName(tableName)
            .withConsistentRead(true)
            .withKey(
                mapOf(
                    KEY_CHANGE_ID to AttributeValue().withS(dynamoEntry.changeId),
                    RANGE_KEY_ID to AttributeValue().withS(dynamoEntry.rangeKey)
                )
            )

        val item = client!!.getItem(request).item
        return if (item != null) ChangeEntryDynamoDB(item).changeEntry else null
    }

    /**
     * LOCK
     */
    fun getLockRepository(tableName: String, indexCreation: Boolean): LockRepository {
        return DynamoDBLockRepository(client!!, tableName, indexCreation)
    }

    fun createLockTable(tableName: String) {
        val mapperConfig = DynamoDBMapperConfig
            .builder()
            .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
            .withPaginationLoadingStrategy(DynamoDBMapperConfig.PaginationLoadingStrategy.EAGER_LOADING)
            .withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(tableName))
            .build()
        TableUtils.createTableIfNotExists(
            client,
            DynamoDBMapper(client, mapperConfig).generateCreateTableRequest(LockEntryDynamoDB::class.java)
                .withProvisionedThroughput(ProvisionedThroughput(1L, 1L))
        )
        TableUtils.waitUntilActive(client, tableName)
    }

    fun isInserted(tableName: String, lockEntry: LockEntry): Boolean {
        return getLockEntry(tableName, lockEntry) != null
    }

    fun getLockEntry(tableName: String, lockEntry: LockEntry): LockEntry? {
        val dynamoEntry = LockEntryDynamoDB(lockEntry)
        val request = GetItemRequest()
            .withTableName(tableName)
            .withConsistentRead(true)
            .withKey(
                mapOf(
                    KEY_FIELD_DYNAMODB to AttributeValue().withS(dynamoEntry.key),
                )
            )

        val item = client!!.getItem(request).item
        return if (item != null) LockEntryDynamoDB(item).lockEntry else null
    }

    fun insertLockEntries(tableName: String, vararg entries: LockEntry) {
        entries.forEach {
            client!!.putItem(PutItemRequest().withTableName(tableName).withItem(LockEntryDynamoDB(it).attributes))
        }
    }

    fun createInsert(tableName: String, vararg entries: LockEntry) {
        createLockTable(tableName)
        insertLockEntries(tableName, *entries)
    }

    fun getDriver(): ConnectionDriver {
        return DynamoDBDriver.withDefaultLock(client!!)
    }


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

val change1 = ChangeEntry(
    "executionId",
    "c1",
    "author-c1",
    Date(),
    ChangeState.EXECUTED,
    ChangeType.EXECUTION,
    "changeLogClass",
    "changeSetMethod",
    0L,
    "executionHostname",
    mapOf("this" to "that")
)

val change1_u = ChangeEntry(
    "executionId",
    "c1",
    "author-c1",
    Date(),
    ChangeState.EXECUTED,
    ChangeType.EXECUTION,
    "UPDATED",
    "UPDATED",
    0L,
    "UPDATED",
    mapOf("UPDATED" to "that")
)

val change2 = ChangeEntry(
    "executionId",
    "c2",
    "author-c2",
    Date(),
    ChangeState.EXECUTED,
    ChangeType.EXECUTION,
    "changeLogClass",
    "changeSetMethod",
    0L,
    "executionHostname",
    mapOf("this" to "that")
)

val change3 = ChangeEntry(
    "executionId",
    "c3",
    "author-c3",
    Date(),
    ChangeState.EXECUTED,
    ChangeType.EXECUTION,
    "changeLogClass",
    "changeSetMethod",
    0L,
    "executionHostname",
    mapOf("this" to "that")
)

val changeFailed = ChangeEntry(
"executionId",
null,
"author-c3",
Date(),
ChangeState.EXECUTED,
ChangeType.EXECUTION,
"changeLogClass",
"changeSetMethod",
0L,
"executionHostname",
mapOf("this" to "that")
)

const val lockKey = "lock-key"

//String key, String status, String owner, Date expiresAt
val lockOwner1NotExpired =
    LockEntry(lockKey, LockStatus.LOCK_HELD.name, "owner-1", Date(System.currentTimeMillis() + 180000))
val lockOwner1NotExpiredUpdated =
    LockEntry(lockKey, LockStatus.LOCK_HELD.name, "owner-1", Date(System.currentTimeMillis() + 360000))
val lockOwner1Expired =
    LockEntry(lockKey, LockStatus.LOCK_HELD.name, "owner-1", Date(System.currentTimeMillis() - 10000))
val lockOwner2NotExpired =
    LockEntry(lockKey, LockStatus.LOCK_HELD.name, "owner-2", Date(System.currentTimeMillis() + 180000))
