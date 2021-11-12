import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.ReturnConsumedCapacity
import com.amazonaws.services.dynamodbv2.model.TransactWriteItemsRequest
import io.mongock.api.exception.MongockException
import io.mongock.driver.api.entry.ChangeEntry
import io.mongock.driver.api.entry.ChangeState
import io.mongock.driver.api.entry.ChangeType
import io.mongock.driver.dynamodb.repository.ChangeEntryDynamoDB
import io.mongock.driver.dynamodb.repository.DynamoDBChangeEntryRepository
import io.mongock.driver.dynamodb.repository.DynamoDBTransactionItems
import org.junit.Test
import org.testcontainers.dynamodb.DynaliteContainer
import org.testcontainers.utility.DockerImageName


class DynamoDBChangeEntryRepositoryITest {
    private var repo: DynamoDBChangeEntryRepository? = null
    private var client: AmazonDynamoDBClient? = null
    private var transactionItems: DynamoDBTransactionItems? = null


    private val DEFAULT_IMAGE_NAME = DockerImageName.parse("quay.io/testcontainers/dynalite")

    @Test
    fun test1() {
        val dynamoDB: DynaliteContainer = DynaliteContainer(DEFAULT_IMAGE_NAME.withTag("v1.2.1-1"))
        dynamoDB.start()

        client = dynamoDB.client as AmazonDynamoDBClient

        repo = DynamoDBChangeEntryRepository(client!!, "mongockChangeLog", true)
        repo!!.initialize()

        ChangeEntryDynamoDB().metadata
        val changeEntry1 = ChangeEntry.createInstance(
            "migrationExecutionId",
            "changeAuthor",
            ChangeState.EXECUTED,
            ChangeType.EXECUTION,
            "changeId-1",
            DynamoDBChangeEntryRepositoryITest::class.java.name,
            "changeSetMethod",
            333,
            "localhost",
            null
        )
        val changeEntry1_1 = ChangeEntry.createInstance(
            "migrationExecutionId",
            "changeAuthor",
            ChangeState.EXECUTED,
            ChangeType.EXECUTION,
            "changeId-1",
            DynamoDBChangeEntryRepositoryITest::class.java.name,
            "NEW_METHOD",
            333,
            "localhost",
            null
        )
        val changeEntry2 = ChangeEntry.createInstance(
            "migrationExecutionId",
            "changeAuthor",
            ChangeState.EXECUTED,
            ChangeType.EXECUTION,
            "changeId-2",
            DynamoDBChangeEntryRepositoryITest::class.java.name,
            "changeSetMethod",
            333,
            "localhost",
            null
        )
        var log = repo!!.entriesLog
        log.forEach { println(it) }

        println("Saving 1")
        repo!!.upsert(changeEntry1)
        log = repo!!.entriesLog
        log.forEach { println(it) }


        println("Saving 2 - transaction")
        transactionItems = DynamoDBTransactionItems()
        repo!!.transactionItems = transactionItems
        repo!!.upsert(changeEntry1_1)

        log = repo!!.entriesLog
        log.forEach { println(it) }

        println("Saving 3")
        repo!!.upsert(changeEntry2)
        log = repo!!.entriesLog
        log.forEach { println(it) }
    }




}