package com.github.cloudyrock.mongock.driver.mongodb.v3.driver;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.mongodb.v3.decorator.impl.MongoDataBaseDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.v3.repository.Mongo3LockRepository;
import com.mongodb.MongoClientException;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.TransactionBody;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.driver.api.driver.TransactionStrategy;
import com.github.cloudyrock.mongock.driver.api.driver.Transactionable;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntryService;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvokerImpl;
import com.github.cloudyrock.mongock.driver.core.driver.ConnectionDriverBase;
import com.github.cloudyrock.mongock.driver.core.lock.LockRepository;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.utils.annotation.NotThreadSafe;
import org.bson.Document;

import java.util.HashSet;
import java.util.Set;

@NotThreadSafe
public abstract class MongoCore3DriverBase<CHANGE_ENTRY extends ChangeEntry>
    extends ConnectionDriverBase<CHANGE_ENTRY>
    implements ConnectionDriver<CHANGE_ENTRY>, Transactionable {

  private static final String DEFAULT_CHANGELOG_COLLECTION_NAME = "mongockChangeLog";
  private static final String DEFAULT_LOCK_COLLECTION_NAME = "mongockLock";

  protected final MongoDatabase mongoDatabase;
  protected String changeLogCollectionName = DEFAULT_CHANGELOG_COLLECTION_NAME;
  protected String lockCollectionName = DEFAULT_LOCK_COLLECTION_NAME;
  protected boolean indexCreation = true;
  protected Mongo3LockRepository lockRepository;
  protected Set<ChangeSetDependency> dependencies;
  protected TransactionStrategy transactionStrategy;
  protected MongoClient mongoClient;
  private TransactionOptions txOptions;

  MongoCore3DriverBase(MongoClient mongoClient,
                       String databaseName,
                       long lockAcquiredForMinutes,
                       long maxWaitingForLockMinutes,
                       int maxTries) {
    this(mongoClient.getDatabase(databaseName), lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries);
    this.mongoClient = mongoClient;
    this.transactionStrategy = TransactionStrategy.MIGRATION;
  }

  MongoCore3DriverBase(MongoDatabase mongoDatabase,
                       long lockAcquiredForMinutes,
                       long maxWaitingForLockMinutes,
                       int maxTries) {
    super(lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries);
    this.mongoDatabase = mongoDatabase;
    this.transactionStrategy = TransactionStrategy.NONE;
  }

  @Override
  public void setChangeLogRepositoryName(String changeLogCollectionName) {
    this.changeLogCollectionName = changeLogCollectionName;
  }

  @Override
  public void setLockRepositoryName(String lockCollectionName) {
    this.lockCollectionName = lockCollectionName;
  }

  @Override
  public String getChangeLogRepositoryName() {
    return changeLogCollectionName;
  }

  @Override
  public String getLockRepositoryName() {
    return lockCollectionName;
  }

  @Override
  public void setIndexCreation(boolean indexCreation) {
    this.indexCreation = indexCreation;
  }

  @Override
  public void runValidation() throws MongockException {
    if (mongoDatabase == null) {
      throw new MongockException("MongoDatabase cannot be null");
    }
    if (this.getLockManager() == null) {
      throw new MongockException("Internal error: Driver needs to be initialized by the runner");
    }
  }

  @Override
  protected LockRepository getLockRepository() {
    if (lockRepository == null) {
      MongoCollection<Document> collection = mongoDatabase.getCollection(lockCollectionName);
      this.lockRepository = new Mongo3LockRepository(collection, indexCreation);
    }
    return lockRepository;
  }

  @Override
  public Set<ChangeSetDependency> getDependencies() {
    if (dependencies == null) {
      throw new MongockException("Driver not initialized");
    }
    return dependencies;
  }

  @Override
  public void specificInitialization() {
    dependencies = new HashSet<>();
    dependencies.add(new ChangeSetDependency(MongoDatabase.class, new MongoDataBaseDecoratorImpl(mongoDatabase, new LockGuardInvokerImpl(getLockManager()))));
    dependencies.add(new ChangeSetDependency(ChangeEntryService.class, getChangeEntryService()));
    this.txOptions = txOptions != null ? txOptions : buildDefaultTxOptions();
  }

  @Override
  public void disableTransaction() {
    this.transactionStrategy = TransactionStrategy.NONE;
  }

  @Override
  public TransactionStrategy getTransactionStrategy() {
    return transactionStrategy;
  }

  @Override
  public void executeInTransaction(Runnable operation) {
    ClientSession clientSession;
    try {
      clientSession = mongoClient.startSession();
    } catch (MongoClientException ex) {
      throw new MongockException("ERROR starting session. If Mongock is connected to a MongoDB cluster which doesn't support transactions, you must to disable transactions", ex);
    }
    try {
      clientSession.withTransaction(getTransactionBody(operation), txOptions);
    } catch (Exception ex) {
      throw new MongockException(ex);
    } finally {
      clientSession.close();
    }
  }

  /**
   * When using Java MongoDB driver directly, it sets the transaction options for all the Mongock's transactions.
   * Default: readPreference: primary, readConcern and writeConcern: majority
   * @param txOptions transaction options
   */
  public void setTransactionOptions(TransactionOptions txOptions) {
    this.txOptions = txOptions;
  }

  private TransactionOptions buildDefaultTxOptions() {
    return TransactionOptions.builder()
        .readPreference(ReadPreference.primary())
        .readConcern(ReadConcern.MAJORITY)
        .writeConcern(WriteConcern.MAJORITY)
        .build();
  }

  private TransactionBody getTransactionBody(Runnable operation) {
    return (TransactionBody<String>) () -> {
      operation.run();
      return "Mongock transaction operation";
    };
  }
}
