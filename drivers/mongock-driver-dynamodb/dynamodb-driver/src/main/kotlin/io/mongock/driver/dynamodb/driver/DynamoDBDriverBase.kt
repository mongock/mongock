package io.mongock.driver.dynamodb.driver

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.datamodeling.TransactionWriteRequest
import com.amazonaws.services.dynamodbv2.model.ReturnConsumedCapacity
import com.amazonaws.services.dynamodbv2.model.TransactWriteItemsRequest
import io.mongock.api.exception.MongockException
import io.mongock.driver.api.driver.ChangeSetDependency
import io.mongock.driver.api.driver.Transactioner
import io.mongock.driver.api.entry.ChangeEntryService
import io.mongock.driver.core.driver.ConnectionDriverBase
import io.mongock.driver.core.lock.LockRepository
import io.mongock.driver.dynamodb.repository.DynamoDBChangeEntryRepository
import io.mongock.driver.dynamodb.repository.DynamoDBLockRepository
import io.mongock.driver.dynamodb.repository.DynamoDBTransactionItems
import mu.KotlinLogging
import java.util.*


private val logger = KotlinLogging.logger {}

abstract class DynamoDBDriverBase protected constructor(
    private val client: AmazonDynamoDBClient,
    lockAcquiredForMillis: Long,
    lockQuitTryingAfterMillis: Long,
    lockTryFrequencyMillis: Long
) :
    ConnectionDriverBase(lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis),
    Transactioner {

    private var transactionItems: DynamoDBTransactionItems? = null
    private var transactionEnabled = false
    private lateinit var _dependencies: MutableSet<ChangeSetDependency>

    private val _changeEntryService: DynamoDBChangeEntryRepository by lazy {
        DynamoDBChangeEntryRepository(client, migrationRepositoryName, indexCreation)
    }

    private val _lockRepository: DynamoDBLockRepository by lazy {
        DynamoDBLockRepository(client, lockRepositoryName, indexCreation)
    }

    override fun getLockRepository(): LockRepository {
        return _lockRepository
    }

    override fun getChangeEntryService(): ChangeEntryService {
        return _changeEntryService
    }

    fun disableTransaction() {
        transactionEnabled = false
    }


    override fun getTransactioner(): Optional<Transactioner> {
        return Optional.ofNullable(if (transactionEnabled) this else null)
    }


    override fun getDependencies(): Set<ChangeSetDependency> {
        if (!this::_dependencies.isInitialized) {
            throw MongockException("Driver not initialized");
        }
        if (transactionItems != null) {
            val transactionReqDependency =
                ChangeSetDependency(DynamoDBTransactionItems::class.java, transactionItems, true)
            _dependencies.remove(transactionReqDependency)
            _dependencies.add(transactionReqDependency)
        }
        return _dependencies;
    }

    override fun specificInitialization() {
        if (!this::_dependencies.isInitialized) {
            _dependencies = HashSet();
        }
    }

    //TODO potentially removable(move it to executeInTransaction)
    override fun prepareForExecutionBlock() {
        transactionItems = DynamoDBTransactionItems()
    }

    override fun executeInTransaction(operation: Runnable) {
        try {
            _changeEntryService.transactionItems = transactionItems
            operation.run()
            if (transactionItems == null || transactionItems?.containsUserTransactions()!!) {
                logger.debug { "no transaction items for changeUnit" }
            }

            val result = client.transactWriteItems(
                TransactWriteItemsRequest()
                    .withTransactItems(transactionItems?.items)
                    .withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL)
            )
            logger.debug { "DynamoDB transaction successful: $result" }
        } catch (ex: Throwable) {
            throw MongockException(ex)
        } finally {
            _changeEntryService.cleanTransactionRequest();
            transactionItems = null
        }
    }

    override fun getLegacyMigrationChangeLogClass(runAlways: Boolean): Class<*> {
        throw NotImplementedError("Legacy migration not provided for DynamoDB")
    }
}