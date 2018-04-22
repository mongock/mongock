package com.github.cloudyrock.mongock;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.jongo.Jongo;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.util.StringUtils.hasText;

public class MongockBuilder {

  //Mandatory
  private final MongoClient mongoClient;
  private String changeLogsScanPackage;
  private String databaseName;

  //Optionals
  private long lockAcquiredForMinutes = 24L * 60L;
  private long maxWaitingForLockMinutes = 3L;
  private int maxTries = 1;
  private boolean throwExceptionIfCannotObtainLock = false;
  private boolean enabled = true;
  private String changeLogCollectionName = "mongockChangeLog";
  private String lockCollectionName = "mongockLock";
  private Environment springEnvironment = null;
  private MongoTemplate mongoTemplate = null;
  private Jongo jongo = null;

  public MongockBuilder(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
  }

  public MongockBuilder setChangeLogCollectionName(String changeLogCollectionName) {
    this.changeLogCollectionName = changeLogCollectionName;
    return this;
  }

  public MongockBuilder setLockCollectionName(String lockCollectionName) {
    this.lockCollectionName = lockCollectionName;
    return this;
  }

  public MongockBuilder setChangeLogsScanPackage(String changeLogsScanPackage) {
    this.changeLogsScanPackage = changeLogsScanPackage;
    return this;
  }

  public MongockBuilder setMongoTemplate(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
    return this;
  }

  public MongockBuilder setJongo(Jongo jongo) {
    this.jongo = jongo;
    return this;
  }

  public MongockBuilder setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
    return this;
  }

  public MongockBuilder setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
    this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
    return this;
  }

  public MongockBuilder setEnabled(boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  public MongockBuilder setSpringEnvironment(Environment springEnvironment) {
    this.springEnvironment = springEnvironment;
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

  public Mongock build() throws MongockException {
    validateMandatoryFields();

    TimeUtils timeUtils = new TimeUtils();

    MongoDatabase database = mongoClient.getDatabase(databaseName);

    //LockChecker
    LockRepository lockRepository = new LockRepository(lockCollectionName, database);

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

    ChangeService changeService = new ChangeService();
    changeService.setChangeLogsBasePackage(changeLogsScanPackage);
    changeService.setEnvironment(springEnvironment);

    final DB db = mongoClient.getDB(databaseName);
    return this.build(
        changeEntryRepository,
        changeService,
        lockChecker,
        proxyFactory.createProxyFromOriginal(mongoClient.getDatabase(databaseName)),
        proxyFactory.createProxyFromOriginal(db),
        proxyFactory.createProxyFromOriginal(mongoTemplate != null ? mongoTemplate : new MongoTemplate(mongoClient, databaseName)),
        proxyFactory.createProxyFromOriginal(jongo != null ? jongo : new Jongo(db))
        );
  }

  Mongock build(ChangeEntryRepository dao,
                ChangeService changeService,
                LockChecker lockChecker,
                MongoDatabase mongoDatabaseProxy,
                DB dbProxy,
                MongoTemplate mongoTemplateProxy,
                Jongo jongoProxy) {
    Mongock mongock = new Mongock(dao, mongoClient,  changeService, lockChecker);
    mongock.setChangelogMongoDatabase(mongoDatabaseProxy);
    mongock.setChangelogDb(dbProxy);
    mongock.setChangelogMongoTemplate(mongoTemplateProxy);
    mongock.setChangelogJongo(jongoProxy);
    mongock.setEnabled(enabled);
    mongock.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    mongock.setSpringEnvironment(springEnvironment);
    return mongock;

  }

  private void validateMandatoryFields() throws MongockException {
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
