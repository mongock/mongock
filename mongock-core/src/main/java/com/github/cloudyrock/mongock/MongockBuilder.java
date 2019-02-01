package com.github.cloudyrock.mongock;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MongockBuilder extends MongockBuilderBase<MongockBuilder> {


  public MongockBuilder(MongoClient mongoClient, String databaseName, String changeLogsScanPackage) {
    super(mongoClient, databaseName, changeLogsScanPackage);
  }

  @Override
  protected MongockBuilder returnInstance() {
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
        proxyFactory.createProxyFromOriginal(mongoClient.getDatabase(databaseName), MongoDatabase.class),
        proxyFactory.createProxyFromOriginal(db, DB.class)
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
}
