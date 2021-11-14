import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import io.mongock.driver.api.entry.ChangeEntry
import io.mongock.driver.api.entry.ChangeState
import io.mongock.driver.api.entry.ChangeType
import io.mongock.driver.dynamodb.repository.ChangeEntryDynamoDB
import io.mongock.driver.dynamodb.repository.DynamoDBChangeEntryRepository
import org.junit.Test
import org.testcontainers.dynamodb.DynaliteContainer
import org.testcontainers.utility.DockerImageName


class DynamoDBChangeEntryRepositoryITest {

    private val DEFAULT_IMAGE_NAME = DockerImageName.parse("quay.io/testcontainers/dynalite")

    @Test
    fun test1() {
//        val dynamoDB: DynaliteContainer = DynaliteContainer(DEFAULT_IMAGE_NAME.withTag("v1.2.1-1"))
//        dynamoDB.start()
//
//        val client = dynamoDB.client as  AmazonDynamoDBClient
//
//        val repo = DynamoDBChangeEntryRepository(client, "mongockChangeLog", true)
//        repo.initialize()
//
//        ChangeEntryDynamoDB().metadata
//        val changeEntry1 = ChangeEntry.createInstance(
//            "migrationExecutionId",
//            "changeAuthor",
//            ChangeState.EXECUTED,
//            ChangeType.EXECUTION,
//            "changeId-1",
//            DynamoDBChangeEntryRepositoryITest::class.java.name,
//            "changeSetMethod",
//            333,
//            "localhost",
//            null
//        )
//        val changeEntry2 = ChangeEntry.createInstance(
//            "migrationExecutionId",
//            "changeAuthor",
//            ChangeState.EXECUTED,
//            ChangeType.EXECUTION,
//            "changeId-2",
//            DynamoDBChangeEntryRepositoryITest::class.java.name,
//            "changeSetMethod",
//            333,
//            "localhost",
//            null
//        )
//        var log = repo.entriesLog
//        log.forEach{ println(it.changeId)}
//        println("Saving 1")
//        repo.saveOrUpdate(changeEntry1)
//        log = repo.entriesLog
//        log.forEach{ println(it.changeId)}
//        println("Saving 2")
//        repo.saveOrUpdate(changeEntry1)
//        log = repo.entriesLog
//        log.forEach{ println(it.changeId)}
//        println("Saving 3")
//        repo.saveOrUpdate(changeEntry2)
//        log = repo.entriesLog
//        log.forEach{ println(it.changeId)}
    }



}