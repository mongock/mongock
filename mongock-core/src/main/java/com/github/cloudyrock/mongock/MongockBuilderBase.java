package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.decorator.impl.MongoDataBaseDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.github.cloudyrock.mongock.decorator.util.MethodInvokerImpl;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import static com.github.cloudyrock.mongock.StringUtils.hasText;

public abstract class MongockBuilderBase<BUILDER_TYPE extends MongockBuilderBase, RETURN_TYPE extends Mongock> {

  //Mandatory
  final MongoClient mongoClient;
  String changeLogsScanPackage;
  String databaseName;

  //Optionals
  long lockAcquiredForMinutes = 24L * 60L;
  long maxWaitingForLockMinutes = 3L;
  int maxTries = 1;
  boolean throwExceptionIfCannotObtainLock = false;
  boolean enabled = true;
  String changeLogCollectionName = "mongockChangeLog";
  String lockCollectionName = "mongockLock";
  String startVersion = "0";
  String endVersion = String.valueOf(Integer.MAX_VALUE);

  //for build
  ChangeEntryRepository changeEntryRepository;
  LockChecker lockChecker;
  MethodInvoker methodInvoker;
  MongoDatabase database;

  /**
   * <p>Builder constructor takes db.mongodb.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see com.mongodb.MongoClient docs
   * </p>
   *
   * @param mongoClient           database connection client
   * @param databaseName          database name
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see MongoClient
   */
  public MongockBuilderBase(MongoClient mongoClient, String databaseName, String changeLogsScanPackage) {
    this.mongoClient = mongoClient;
    this.databaseName = databaseName;
    this.changeLogsScanPackage = changeLogsScanPackage;
  }

  protected abstract BUILDER_TYPE returnInstance();

  /**
   * <p>Changes the changelog collection name</p>
   * <p>Be careful as changing the changelog collection name can make Mongock to undesirably run twice the same changelog</p>
   *
   * @param changeLogCollectionName name of the collection
   * @return Mongock builder
   */
  public BUILDER_TYPE setChangeLogCollectionName(String changeLogCollectionName) {
    this.changeLogCollectionName = changeLogCollectionName;
    return returnInstance();
  }

  /**
   * <p>Changes the lock collection name</p>
   * <p>Be careful as changing the lock collection name can make Mongock to run twice the same changelog and other
   * undesirable scenarios</p>
   *
   * @param lockCollectionName name of the collection
   * @return Mongock builder
   */
  public BUILDER_TYPE setLockCollectionName(String lockCollectionName) {
    this.lockCollectionName = lockCollectionName;
    return returnInstance();
  }

  /**
   * Feature which enables/disables throwing MongockException if the lock cannot be obtained
   *
   * @param throwExceptionIfCannotObtainLock Mongock will throw MongockException if lock can not be obtained
   * @return Mongock builder
   */
  public BUILDER_TYPE setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
    this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
    return returnInstance();
  }


  /**
   * Feature which enables/disables execution
   *
   * @param enabled Migration process will run only if this option is set to true
   * @return Mongock builder
   */
  public BUILDER_TYPE setEnabled(boolean enabled) {
    this.enabled = enabled;
    return returnInstance();
  }

  /**
   * Set up the lock with minimal configuration. This implies Mongock will throw an exception if cannot obtains the lock.
   *
   * @param lockAcquiredForMinutes   Acquired time in minutes
   * @param maxWaitingForLockMinutes max time in minutes to wait for the lock in each try.
   * @param maxTries                 number of tries
   * @return Mongock object for fluent interface
   */
  public BUILDER_TYPE setLockConfig(long lockAcquiredForMinutes, long maxWaitingForLockMinutes, int maxTries) {
    this.lockAcquiredForMinutes = lockAcquiredForMinutes;
    this.maxWaitingForLockMinutes = maxWaitingForLockMinutes;
    this.maxTries = maxTries;
    this.throwExceptionIfCannotObtainLock = true;
    return returnInstance();
  }

  /**
   * Set up the lock with default configuration to wait for it and through an exception when cannot obtain it.
   *
   * @return Mongock object for fluent interface
   */
  public BUILDER_TYPE setLockQuickConfig() {
    setLockConfig(3, 4, 3);
    return returnInstance();
  }

  /**
   * Set up the start Version for versioned schema changes
   * 
   * @param startVersion
   *          Version to start with
   */
  public BUILDER_TYPE setStartVersion(String startVersion) {
    this.startVersion = startVersion;
    return returnInstance();
  }

  /**
   * Set up the end Version for versioned schema changes
   * 
   * @param endVersion
   *          Version to end with
   */
  public BUILDER_TYPE setEndVersion(String endVersion) {
    this.endVersion = endVersion;
    return returnInstance();
  }

  void validateMandatoryFields() throws MongockException {
    if (mongoClient == null) {
      throw new MongockException("MongoClient cannot be null");
    }
    if (!hasText(databaseName)) {
      throw new MongockException("DB name is not set. It should be defined in MongoDB URI or via setter");
    }
    if (!hasText(changeLogsScanPackage)) {
      throw new MongockException("Scan package for changelogs is not set: use appropriate setter");
    }
  }


  public RETURN_TYPE build() {
    validateMandatoryFields();
    database = mongoClient.getDatabase(databaseName);

    lockChecker = createLockChecker();
    methodInvoker = new MethodInvokerImpl(lockChecker);
    changeEntryRepository = createChangeRepository();
    return this.createBuild();
  }

  private LockChecker createLockChecker() {
    LockRepository lockRepository = new LockRepository(lockCollectionName, database);
    lockRepository.ensureIndex();

    TimeUtils timeUtils = new TimeUtils();
    return new LockChecker(lockRepository, timeUtils)
        .setLockAcquiredForMillis(timeUtils.minutesToMillis(lockAcquiredForMinutes))
        .setLockMaxTries(maxTries)
        .setLockMaxWaitMillis(timeUtils.minutesToMillis(maxWaitingForLockMinutes));
  }


  private ChangeEntryRepository createChangeRepository() {
    ChangeEntryRepository changeEntryRepository = new ChangeEntryRepository(changeLogCollectionName, database);
    changeEntryRepository.ensureIndex();
    return changeEntryRepository;
  }

  ChangeService createChangeService() {
        ChangeService changeService = new ChangeService();
    changeService.setChangeLogsBasePackage(changeLogsScanPackage);
        changeService.setStartVersion(startVersion);
        changeService.setEndVersion(endVersion);
    return changeService;
  }

  MongoDatabase createMongoDataBaseProxy() {
    return new MongoDataBaseDecoratorImpl(mongoClient.getDatabase(databaseName), methodInvoker);
  }

  abstract RETURN_TYPE createBuild();


}
