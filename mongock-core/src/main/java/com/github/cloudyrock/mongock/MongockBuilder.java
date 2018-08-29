package com.github.cloudyrock.mongock;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.github.cloudyrock.mongock.StringUtils.hasText;

public class MongockBuilder {

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


  /**
   * <p>Builder constructor takes db.mongodb.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about <tt>MongoClient</tt> please see com.mongodb.MongoClient docs
   * </p>
   * @param mongoClient database connection client
   * @param databaseName database name
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see MongoClient
   */
  public MongockBuilder(MongoClient mongoClient, String databaseName, String changeLogsScanPackage) {
    this.mongoClient = mongoClient;
    this.databaseName = databaseName;
    this.changeLogsScanPackage = changeLogsScanPackage;
  }

  /**
   * <p>Changes the changelog collection name</p>
   * <p>Be careful as changing the changelog collection name can make Mongock to undesirably run twice the same changelog</p>
   * @param changeLogCollectionName name of the collection
   * @return Mongock builder
   */
  public MongockBuilder setChangeLogCollectionName(String changeLogCollectionName) {
    this.changeLogCollectionName = changeLogCollectionName;
    return this;
  }

  /**
   * <p>Changes the lock collection name</p>
   * <p>Be careful as changing the lock collection name can make Mongock to run twice the same changelog and other
   * undesirable scenarios</p>
   * @param lockCollectionName name of the collection
   * @return Mongock builder
   */
  public MongockBuilder setLockCollectionName(String lockCollectionName) {
    this.lockCollectionName = lockCollectionName;
    return this;
  }

  /**
   * Feature which enables/disables throwing MongockException if the lock cannot be obtained
   *
   * @param throwExceptionIfCannotObtainLock Mongock will throw MongockException if lock can not be obtained
   * @return Mongock builder
   */
  public MongockBuilder setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
    this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
    return this;
  }


  /**
   * Feature which enables/disables execution
   *
   * @param enabled Migration process will run only if this option is set to true
   * @return Mongock builder
   */
  public MongockBuilder setEnabled(boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  /**
   * Set up the lock with minimal configuration. This implies Mongock will throw an exception if cannot obtains the lock.
   *
   * @param lockAcquiredForMinutes   Acquired time in minutes
   * @param maxWaitingForLockMinutes max time in minutes to wait for the lock in each try.
   * @param maxTries                 number of tries
   * @return Mongock object for fluent interface
   */
  public MongockBuilder setLockConfig(long lockAcquiredForMinutes, long maxWaitingForLockMinutes, int maxTries) {
    this.lockAcquiredForMinutes = lockAcquiredForMinutes;
    this.maxWaitingForLockMinutes = maxWaitingForLockMinutes;
    this.maxTries = maxTries;
    this.throwExceptionIfCannotObtainLock = true;
    return this;
  }

  /**
   * Set up the lock with default configuration to wait for it and through an exception when cannot obtain it.
   *
   * @return Mongock object for fluent interface
   */
  public MongockBuilder setLockQuickConfig() {
    setLockConfig(3, 4, 3);
    return this;
  }

  public Mongock build() {
    validateMandatoryFields();

    TimeUtils timeUtils = new TimeUtils();

    MongoDatabase database = mongoClient.getDatabase(databaseName);

    //LockChecker
    LockRepository lockRepository = new LockRepository(lockCollectionName, database);
    lockRepository.ensureIndex();

    final LockChecker lockChecker = new LockChecker(lockRepository, timeUtils)
        .setLockAcquiredForMillis(timeUtils.minutesToMillis(lockAcquiredForMinutes))
        .setLockMaxTries(maxTries)
        .setLockMaxWaitMillis(timeUtils.minutesToMillis(maxWaitingForLockMinutes));

    //Proxy
    PreInterceptor preInterceptor = new PreInterceptor() {
      @Override
      public void before() {
        lockChecker.ensureLockDefault();
      }
    };

    final Set<String> proxyCreatorAndUnchackedmethods = new HashSet<>(
        Arrays.asList("getCollection", "getCollectionFromString", "getDatabase", "toString"));

    ProxyFactory proxyFactory =
        new ProxyFactory(preInterceptor, proxyCreatorAndUnchackedmethods, proxyCreatorAndUnchackedmethods);

    //ChangeService
    ChangeEntryRepository changeEntryRepository = new ChangeEntryRepository(changeLogCollectionName, database);
    changeEntryRepository.ensureIndex();

    ChangeService changeService = new ChangeService();
    changeService.setChangeLogsBasePackage(changeLogsScanPackage);

    final DB db = mongoClient.getDB(databaseName);
    return this.build(
        changeEntryRepository,
        changeService,
        lockChecker,
        proxyFactory.createProxyFromOriginal(mongoClient.getDatabase(databaseName)),
        proxyFactory.createProxyFromOriginal(db)
        );
  }

  Mongock build(ChangeEntryRepository changeEntryRepository,
                ChangeService changeService,
                LockChecker lockChecker,
                MongoDatabase mongoDatabaseProxy,
                DB dbProxy) {
    Mongock mongock = new Mongock(changeEntryRepository, mongoClient,  changeService, lockChecker);
    mongock.setChangelogMongoDatabase(mongoDatabaseProxy);
    mongock.setChangelogDb(dbProxy);
    mongock.setEnabled(enabled);
    mongock.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    return mongock;

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
}
