package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.decorator.impl.MongoDataBaseDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.github.cloudyrock.mongock.decorator.util.MethodInvokerImpl;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.io.Closeable;
import java.util.Map;

import static com.github.cloudyrock.mongock.StringUtils.hasText;

public abstract class MongockBuilderBase<BUILDER_TYPE extends MongockBuilderBase, MONGOCK_TYPE extends Mongock> {

  //Mandatory
  final com.mongodb.MongoClient legacyMongoClient;
  final MongoClient mongoClient;
  String changeLogsScanPackage;
  String databaseName;

  //Optionals
  private long lockAcquiredForMinutes = 24L * 60L;
  private long maxWaitingForLockMinutes = 3L;
  private int maxTries = 1;
  boolean throwExceptionIfCannotObtainLock = false;
  boolean enabled = true;
  private String changeLogCollectionName = "mongockChangeLog";
  private String lockCollectionName = "mongockLock";
  private String startSystemVersion = "0";
  private String endSystemVersion = String.valueOf(Integer.MAX_VALUE);
  private Map<String, Object> metadata;

  //for build
  ChangeEntryRepository changeEntryRepository;
  LockChecker lockChecker;
  MethodInvoker methodInvoker;
  private MongoDatabase database;

  /**
   * <p>Builder constructor takes the new API MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see com.mongodb.MongoClient docs
   * </p>
   *
   * @param newMongoClient        database connection client
   * @param databaseName          database name
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see MongoClient
   */
  public MongockBuilderBase(MongoClient newMongoClient, String databaseName, String changeLogsScanPackage) {
    this(newMongoClient, null, databaseName, changeLogsScanPackage);
  }

  /**
   * <p>Builder constructor takes db.mongodb.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see om.mongodb.client.MongoClient docs
   * </p>
   *
   * @param legacyMongoClient     database connection client
   * @param databaseName          database name
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see com.mongodb.MongoClient
   */
  @Deprecated
  public MongockBuilderBase(com.mongodb.MongoClient legacyMongoClient, String databaseName, String changeLogsScanPackage) {
    this(null, legacyMongoClient, databaseName, changeLogsScanPackage);
  }

  private MongockBuilderBase(MongoClient mongoClient,
                             com.mongodb.MongoClient legacyMongoClient,
                             String databaseName,
                             String changeLogsScanPackage) {
    this.mongoClient = mongoClient;
    this.legacyMongoClient = legacyMongoClient;
    this.databaseName = databaseName;
    this.changeLogsScanPackage = changeLogsScanPackage;
  }

  /**
   * Feature which enables/disables throwing MongockException if the lock cannot be obtained
   *
   * @param throwExceptionIfCannotObtainLock Mongock will throw MongockException if lock can not be obtained
   * @return Mongock builder for fluent interface
   */
  public BUILDER_TYPE setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
    this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
    return returnInstance();
  }


  /**
   * Feature which enables/disables execution
   *
   * @param enabled Migration process will run only if this option is set to true
   * @return Mongock builder for fluent interface
   */
  public BUILDER_TYPE setEnabled(boolean enabled) {
    this.enabled = enabled;
    return returnInstance();
  }

  /**
   * Sets up the lock with minimal configuration. This implies Mongock will throw an exception if cannot obtains the lock.
   *
   * @param lockAcquiredForMinutes   Acquired time in minutes
   * @param maxWaitingForLockMinutes max time in minutes to wait for the lock in each try.
   * @param maxTries                 number of tries
   * @return Mongock builder for fluent interface
   */
  public BUILDER_TYPE setLockConfig(long lockAcquiredForMinutes, long maxWaitingForLockMinutes, int maxTries) {
    this.lockAcquiredForMinutes = lockAcquiredForMinutes;
    this.maxWaitingForLockMinutes = maxWaitingForLockMinutes;
    this.maxTries = maxTries;
    this.throwExceptionIfCannotObtainLock = true;
    return returnInstance();
  }

  /**
   * Sets up the lock with default configuration to wait for it and through an exception when cannot obtain it.
   *
   * @return Mongock builder for fluent interface
   */
  public BUILDER_TYPE setLockQuickConfig() {
    setLockConfig(3, 4, 3);
    return returnInstance();
  }

  /**
   * Sets changeLog collection name
   * @param changeLogCollectionName changeLog collection name
   * @return
   */
  public BUILDER_TYPE setChangeLogCollectionName(String changeLogCollectionName) {
    if(changeLogCollectionName == null || "".equals(changeLogCollectionName)) {
      throw new MongockException("invalid changeLog collection name");
    }
    this.changeLogCollectionName = changeLogCollectionName;
    return returnInstance();
  }

  /**
   * Sets the lock collection name
   * @param lockCollectionName lock collection name
   * @return
   */
  public BUILDER_TYPE setLockCollectionName(String lockCollectionName) {
    if(lockCollectionName == null || "".equals(lockCollectionName)) {
      throw new MongockException("invalid changeLog collection name");
    }
    this.lockCollectionName = lockCollectionName;
    return returnInstance();
  }


  /**
   * Sets up the start Version for versioned schema changes.
   * This shouldn't be confused with the changeSet systemVersion. This is from a consultancy point of view.
   * So the changeSet are tagged with a systemVersion and then when building Mongock, you specify
   * the systemVersion range you want to apply, so all the changeSets tagget with systemVersion inside that
   * range will be applied
   *
   * @param startSystemVersion Version to start with
   * @return Mongock builder for fluent interface
   */
  public BUILDER_TYPE setStartSystemVersion(String startSystemVersion) {
    this.startSystemVersion = startSystemVersion;
    return returnInstance();
  }

  /**
   * Sets up the end Version for versioned schema changes.
   * This shouldn't be confused with the changeSet systemVersion. This is from a consultancy point of view.
   * So the changeSet are tagged with a systemVersion and then when building Mongock, you specify
   * the systemVersion range you want to apply, so all the changeSets tagget with systemVersion inside that
   * range will be applied
   *
   * @param endSystemVersion Version to end with
   * @return Mongock builder for fluent interface
   */
  public BUILDER_TYPE setEndSystemVersion(String endSystemVersion) {
    this.endSystemVersion = endSystemVersion;
    return returnInstance();
  }

  /**
   * Sets the metadata for the mongock process. This metadata will be added to each document in the mongockChangeLog
   * collection. This is useful when the system needs to add some extra info to the changeLog.
   *
   * @param metadata Custom metadata object  to be added
   * @return Mongock builder for fluent interface
   */
  public BUILDER_TYPE withMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
    return returnInstance();
  }

  void validateMandatoryFields() throws MongockException {
    if (legacyMongoClient == null && mongoClient == null) {
      throw new MongockException("MongoClient cannot be null");
    }
    if (!hasText(databaseName)) {
      throw new MongockException("DB name is not set. It should be defined in MongoDB URI or via setter");
    }
    if (!hasText(changeLogsScanPackage)) {
      throw new MongockException("Scan package for changelogs is not set: use appropriate setter");
    }
  }


  public MONGOCK_TYPE build() {
    validateMandatoryFields();
    database = getMongoDatabase();
    lockChecker = createLockChecker();
    methodInvoker = new MethodInvokerImpl(lockChecker);
    changeEntryRepository = createChangeRepository();
    MONGOCK_TYPE mongock = this.createMongockInstance();
    mongock.addChangeSetDependency(MongoDatabase.class, createMongoDataBaseProxy());
    mongock.setEnabled(enabled);
    mongock.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    mongock.setMetadata(this.metadata);
    return mongock;
  }

  MongoDatabase getMongoDatabase() {
    return mongoClient != null ? mongoClient.getDatabase(databaseName) : legacyMongoClient.getDatabase(databaseName);
  }

  Closeable getMongoClientCloseable() {
    return mongoClient != null ? mongoClient : legacyMongoClient;
  }

  private LockChecker createLockChecker() {
    LockRepository lockRepository = new LockMongoRepository(lockCollectionName, database);
    lockRepository.initialize();

    TimeUtils timeUtils = new TimeUtils();
    return new LockChecker(lockRepository, timeUtils)
        .setLockAcquiredForMillis(timeUtils.minutesToMillis(lockAcquiredForMinutes))
        .setLockMaxTries(maxTries)
        .setLockMaxWaitMillis(timeUtils.minutesToMillis(maxWaitingForLockMinutes));
  }


  private ChangeEntryRepository createChangeRepository() {
    ChangeEntryRepository changeEntryRepository = new ChangeEntryMongoRepository(changeLogCollectionName, database);
    changeEntryRepository.initialize();
    return changeEntryRepository;
  }

  private MongoDatabase createMongoDataBaseProxy() {
    return new MongoDataBaseDecoratorImpl(getMongoDatabase(), methodInvoker);
  }

  protected final ChangeService createChangeService() {
    ChangeService changeService = createChangeServiceInstance();
    changeService.setChangeLogsBasePackage(changeLogsScanPackage);
    changeService.setStartVersion(startSystemVersion);
    changeService.setEndVersion(endSystemVersion);
    return changeService;
  }

  protected abstract MONGOCK_TYPE createMongockInstance();
  protected abstract ChangeService createChangeServiceInstance();
  protected abstract BUILDER_TYPE returnInstance();



}
