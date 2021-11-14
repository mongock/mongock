import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.GetItemRequest
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.amazonaws.services.dynamodbv2.util.TableUtils
import io.kotest.core.spec.style.scopes.DescribeSpecContainerContext
import io.kotest.core.test.TestContext
import io.mongock.driver.api.entry.ChangeEntry
import io.mongock.driver.api.entry.ChangeEntry.KEY_CHANGE_ID
import io.mongock.driver.api.entry.ChangeEntryService
import io.mongock.driver.api.entry.ChangeState
import io.mongock.driver.api.entry.ChangeType
import io.mongock.driver.dynamodb.repository.ChangeEntryDynamoDB
import io.mongock.driver.dynamodb.repository.DynamoDBChangeEntryRepository
import io.mongock.driver.dynamodb.repository.RANGE_KEY_ID
import org.testcontainers.dynamodb.DynaliteContainer
import org.testcontainers.utility.DockerImageName
import java.util.*


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

suspend fun DescribeSpecContainerContext.When(
    name: String,
    preTest: () -> Unit,
    test: suspend TestContext.() -> Unit
) {
    preTest()
    When(name, test)
}

suspend fun DescribeSpecContainerContext.describe(
    name: String,
    preTest: () -> Unit,
    test: suspend TestContext.() -> Unit
) {
    preTest()
    describe(name, test)
}

val c1 = ChangeEntry(
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

val c1_updated = ChangeEntry(
    "executionId",
    "c1",
    "author-c1",
    Date(),
    ChangeState.EXECUTED,
    ChangeType.EXECUTION,
    "updated-changelog-class",
    "updated-changelog-method",
    0L,
    "updated-host-name",
    mapOf("updated" to "that")
)

val c2 = ChangeEntry(
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

val c3 = ChangeEntry(
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

class DynamoDBTestCompanion {

    private var container: DynaliteContainer? = null
    private var client: AmazonDynamoDBClient? = null
    private var dynamoDB: DynamoDB? = null
    fun startContainer() {
        container = DynaliteContainer(DockerImageName.parse("quay.io/testcontainers/dynalite").withTag("v1.2.1-1"))
        container!!.start()
        client = container!!.client as AmazonDynamoDBClient
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

    fun dropTable(tableName: String) {
        dynamoDB!!.getTable(tableName).delete()
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


}