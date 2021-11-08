package io.mongock.driver.dynamodb.driver

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.datamodeling.TransactionWriteRequest
import com.amazonaws.services.dynamodbv2.model.TransactWriteItemsRequest
import io.mongock.api.exception.MongockException
import io.mongock.driver.api.driver.ChangeSetDependency
import io.mongock.driver.api.driver.Transactioner
import io.mongock.driver.api.entry.ChangeEntryService
import io.mongock.driver.core.driver.ConnectionDriverBase
import io.mongock.driver.core.lock.LockRepository
import io.mongock.driver.dynamodb.repository.DynamoDBChangeEntryRepository
import io.mongock.driver.dynamodb.repository.DynamoDBLockRepository
import mu.KotlinLogging
import org.slf4j.Logger
import java.util.*


private val logger = KotlinLogging.logger {}

class DynamoDBDriverBase(
    private val client: AmazonDynamoDBClient,
    lockAcquiredForMillis: Long,
    lockQuitTryingAfterMillis: Long,
    lockTryFrequencyMillis: Long
) :
    ConnectionDriverBase(lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis),
    Transactioner {

    private var transactionRequest:TransactWriteItemsRequest? = null
    private var transactionEnabled = false
    private lateinit var _lockRepository: LockRepository
    private lateinit var _changeEntryService: ChangeEntryService
    private lateinit var _dependencies: MutableSet<ChangeSetDependency>

    @Synchronized
    override fun getLockRepository(): LockRepository {
        if (!this::_lockRepository.isInitialized) {
            _lockRepository = DynamoDBLockRepository(client = client, tableName = lockRepositoryName)
            _lockRepository.setIndexCreation(isIndexCreation)
        }
        return _lockRepository
    }

    @Synchronized
    override fun getChangeEntryService(): ChangeEntryService {
        if (!this::_changeEntryService.isInitialized) {
            _changeEntryService = DynamoDBChangeEntryRepository(client = client, tableName = migrationRepositoryName)
            _lockRepository.setIndexCreation(isIndexCreation)
        }
        return _changeEntryService
    }

    fun disableTransaction() {
        transactionEnabled = false
    }


    override fun getTransactioner(): Optional<Transactioner> {
        return Optional.ofNullable(if (transactionEnabled) this else null)
    }

    override fun prepareForExecutionBlock() {
        transactionRequest = TransactWriteItemsRequest();
    }

    override fun getDependencies(): Set<ChangeSetDependency> {
        if (!this::_dependencies.isInitialized) {
            throw MongockException("Driver not initialized");
        }
        if(transactionRequest != null) {
            val transactionReqDependency = ChangeSetDependency(TransactionWriteRequest::class.java, transactionRequest, true)
            _dependencies.remove(transactionReqDependency)
            _dependencies.add(transactionReqDependency)
        }
        return _dependencies;
    }

    override fun specificInitialization() {
        if(!this::_dependencies.isInitialized) {
            _dependencies = HashSet();
        }
    }

    override fun executeInTransaction(operation: Runnable) {
        try {
            if(transactionRequest == null) {
                throw MongockException("executing in transaction, but driver hasn't been prepared for transaction: transaction request is null")
            }
            if(transactionRequest?.transactItems == null || transactionRequest?.transactItems?.size == 0) {
                logger.debug { "no transaction items for changeUnit" }
            }

        } catch (ex: Error) {

        } finally {

        }
    }

    override fun getLegacyMigrationChangeLogClass(runAlways: Boolean): Class<*> {
        throw NotImplementedError("Legacy migration not provided for DynamoDB")
    }
}