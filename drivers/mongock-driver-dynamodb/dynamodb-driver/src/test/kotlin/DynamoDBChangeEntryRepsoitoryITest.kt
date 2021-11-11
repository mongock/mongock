import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import io.mongock.driver.dynamodb.repository.DynamoDBChangeEntryRepository
import org.junit.Test
import org.testcontainers.dynamodb.DynaliteContainer
import org.testcontainers.utility.DockerImageName


class DynamoDBChangeEntryRepositoryITest {

    private val DEFAULT_IMAGE_NAME = DockerImageName.parse("quay.io/testcontainers/dynalite")

    @Test
    fun test1() {
        val dynamoDB: DynaliteContainer = DynaliteContainer(DEFAULT_IMAGE_NAME.withTag("v1.2.1-1"))
        dynamoDB.start()

        val client = dynamoDB.client as  AmazonDynamoDBClient

        val repo = DynamoDBChangeEntryRepository(client, "mongockChangeLog", true)
        repo.initialize()


        val log = repo.entriesLog
        log.forEach{ println(it)}
    }



}