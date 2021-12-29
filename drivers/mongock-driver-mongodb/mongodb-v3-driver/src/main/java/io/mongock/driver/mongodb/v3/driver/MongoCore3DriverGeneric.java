package io.mongock.driver.mongodb.v3.driver;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoDatabase;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.driver.ChangeSetDependency;
import io.mongock.driver.api.driver.DriverLegaciable;
import io.mongock.driver.api.entry.ChangeEntryService;
import io.mongock.driver.core.driver.TransactionalConnectionDriverBase;
import io.mongock.driver.mongodb.v3.changelogs.runalways.MongockV3LegacyMigrationChangeRunAlwaysLog;
import io.mongock.driver.mongodb.v3.changelogs.runonce.MongockV3LegacyMigrationChangeLog;
import io.mongock.driver.mongodb.v3.repository.Mongo3ChangeEntryRepository;
import io.mongock.driver.mongodb.v3.repository.Mongo3LockRepository;
import io.mongock.driver.mongodb.v3.repository.ReadWriteConfiguration;
import io.mongock.utils.annotation.NotThreadSafe;

@NotThreadSafe
public abstract class MongoCore3DriverGeneric extends TransactionalConnectionDriverBase implements DriverLegaciable {


  private static final WriteConcern DEFAULT_WRITE_CONCERN = WriteConcern.MAJORITY.withJournal(true);
  private static final ReadConcern DEFAULT_READ_CONCERN = ReadConcern.MAJORITY;
  private static final ReadPreference DEFAULT_READ_PREFERENCE = ReadPreference.primary();

  private WriteConcern writeConcern;
  private ReadConcern readConcern;
  private ReadPreference readPreference;
  protected TransactionOptions txOptions;
  protected final MongoDatabase mongoDatabase;

  protected MongoCore3DriverGeneric(MongoDatabase mongoDatabase,
                                    long lockAcquiredForMillis,
                                    long lockQuitTryingAfterMillis,
                                    long lockTryFrequencyMillis) {
    super(lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
    this.mongoDatabase = mongoDatabase;
  }


  @Override
  protected void beforeParentInitialization() {
    lockRepository = new Mongo3LockRepository(mongoDatabase.getCollection(getLockRepositoryName()), getReadWriteConfiguration());
    lockRepository.setIndexCreation(isIndexCreation());

    changeEntryRepository = new Mongo3ChangeEntryRepository(mongoDatabase.getCollection(getMigrationRepositoryName()), getReadWriteConfiguration());
    changeEntryRepository.setIndexCreation(isIndexCreation());
  }

  @Override
  public void afterParentInitialization() {
    dependencies.add(new ChangeSetDependency(MongoDatabase.class, mongoDatabase, true));
    dependencies.add(new ChangeSetDependency(ChangeEntryService.class, getChangeEntryService(), false));
    txOptions = TransactionOptions.builder()
        .writeConcern(getWriteConcern())
        .readConcern(getReadConcern())
        .readPreference(getReadPreference())
        .build();
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

  //LEGACIALE
  @Override
  public Class getLegacyMigrationChangeLogClass(boolean runAlways) {
    return runAlways ? MongockV3LegacyMigrationChangeRunAlwaysLog.class : MongockV3LegacyMigrationChangeLog.class;
  }

  //SPECIFIC MONGODB CONFIGURATION
  public void setWriteConcern(WriteConcern writeConcern) {
    this.writeConcern = writeConcern;
  }

  public void setReadConcern(ReadConcern readConcern) {
    this.readConcern = readConcern;
  }

  public void setReadPreference(ReadPreference readPreference) {
    this.readPreference = readPreference;
  }

  protected ReadWriteConfiguration getReadWriteConfiguration() {
    return new ReadWriteConfiguration(
        getWriteConcern(),
        getReadConcern(),
        getReadPreference()
    );
  }

  protected ReadPreference getReadPreference() {
    return readPreference != null ? readPreference : DEFAULT_READ_PREFERENCE;
  }

  protected ReadConcern getReadConcern() {
    return readConcern != null ? readConcern : DEFAULT_READ_CONCERN;
  }

  protected WriteConcern getWriteConcern() {
    return writeConcern != null ? writeConcern : DEFAULT_WRITE_CONCERN;
  }


  /**
   * Will be removed in next major release.
   *
   * If not set already will set the writeConcern, readConcern and readPreference
   * Use instead setWriteConcern, setReadConcern and
   */
  @Deprecated
  public void setTransactionOptions(TransactionOptions txOptions) {
    if(writeConcern == null) {
      setWriteConcern(txOptions.getWriteConcern());
    }
    if(readConcern == null) {
      setReadConcern(txOptions.getReadConcern());
    }
    if(readPreference == null) {
      setReadPreference(txOptions.getReadPreference());
    }
  }

}
