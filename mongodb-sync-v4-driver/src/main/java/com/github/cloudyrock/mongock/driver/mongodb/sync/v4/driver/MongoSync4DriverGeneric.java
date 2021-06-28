package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.driver;

import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.driver.api.driver.Transactioner;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntryService;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvokerImpl;
import com.github.cloudyrock.mongock.driver.core.driver.ConnectionDriverBase;
import com.github.cloudyrock.mongock.driver.core.lock.LockRepositoryWithEntity;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.changelogs.runalways.MongockSync4LegacyMigrationChangeRunAlwaysLog;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.changelogs.runonce.MongockSync4LegacyMigrationChangeLog;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.decorator.impl.MongoDataBaseDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.repository.MongoSync4ChangeEntryRepository;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.repository.MongoSync4LockRepository;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.repository.ReadWriteConfiguration;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.utils.annotation.NotThreadSafe;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.HashSet;
import java.util.Set;

@NotThreadSafe
public abstract class MongoSync4DriverGeneric<CHANGE_ENTRY extends ChangeEntry> extends ConnectionDriverBase<CHANGE_ENTRY> implements Transactioner {

  private static final String DEFAULT_CHANGELOG_COLLECTION_NAME = "mongockChangeLog";
  private static final String DEFAULT_LOCK_COLLECTION_NAME = "mongockLock";

  private static final WriteConcern DEFAULT_WRITE_CONCERN = WriteConcern.MAJORITY.withJournal(true);
  private static final ReadConcern DEFAULT_READ_CONCERN = ReadConcern.MAJORITY;
  private static final ReadPreference DEFAULT_READ_PREFERENCE = ReadPreference.primary();

  protected String changeLogCollectionName = DEFAULT_CHANGELOG_COLLECTION_NAME;
  protected String lockCollectionName = DEFAULT_LOCK_COLLECTION_NAME;
  protected boolean indexCreation = true;

  protected MongoSync4ChangeEntryRepository<CHANGE_ENTRY> changeEntryRepository;
  protected MongoSync4LockRepository lockRepository;
  protected Set<ChangeSetDependency> dependencies;
  protected TransactionOptions txOptions;
  private WriteConcern writeConcern;
  private ReadConcern readConcern;
  private ReadPreference readPreference;
  protected final MongoDatabase mongoDatabase;

  protected MongoSync4DriverGeneric(MongoDatabase mongoDatabase,
                                    long lockAcquiredForMillis,
                                    long lockQuitTryingAfterMillis,
                                    long lockTryFrequencyMillis) {
    super(lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
    this.mongoDatabase = mongoDatabase;
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

  /**
   * When using Java MongoDB driver directly, it sets the transaction options for all the Mongock's transactions.
   * Default: readPreference: primary, readConcern and writeConcern: majority
   *
   * @param txOptions transaction options
   */
  public void setTransactionOptions(TransactionOptions txOptions) {
    this.txOptions = txOptions;
  }

  public void setWriteConcern(WriteConcern writeConcern) {
    this.writeConcern = writeConcern;
  }

  public void setReadConcern(ReadConcern readConcern) {
    this.readConcern = readConcern;
  }

  public void setReadPreference(ReadPreference readPreference) {
    this.readPreference = readPreference;
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
  protected LockRepositoryWithEntity getLockRepository() {
    if (lockRepository == null) {
      MongoCollection<Document> collection = mongoDatabase.getCollection(lockCollectionName);
      this.lockRepository = new MongoSync4LockRepository(collection, indexCreation, getReadWriteConfiguration());
    }
    return lockRepository;
  }

  @Override
  public ChangeEntryService<CHANGE_ENTRY> getChangeEntryService() {
    if (changeEntryRepository == null) {
      this.changeEntryRepository = new MongoSync4ChangeEntryRepository<>(mongoDatabase.getCollection(changeLogCollectionName), indexCreation, getReadWriteConfiguration());
    }
    return changeEntryRepository;
  }

  @Override
  public Class getLegacyMigrationChangeLogClass(boolean runAlways) {
    return runAlways ? MongockSync4LegacyMigrationChangeRunAlwaysLog.class : MongockSync4LegacyMigrationChangeLog.class;
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

  private TransactionOptions buildDefaultTxOptions() {
    return TransactionOptions.builder()
        .readPreference(ReadPreference.primary())
        .readConcern(ReadConcern.MAJORITY)
        .writeConcern(WriteConcern.MAJORITY)
        .build();
  }

  protected ReadWriteConfiguration getReadWriteConfiguration() {
    return new ReadWriteConfiguration(
        writeConcern != null ? writeConcern : DEFAULT_WRITE_CONCERN,
        readConcern != null ? readConcern : DEFAULT_READ_CONCERN,
        readPreference != null ? readPreference : DEFAULT_READ_PREFERENCE
    );
  }

}
