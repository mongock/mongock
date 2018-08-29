package com.github.cloudyrock.mongock;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SpringMongockBuilder extends MongockBuilder {

  private Environment springEnvironment = null;
  private MongoTemplate mongoTemplate = null;

  /**
   * <p>Builder constructor takes db.mongodb.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about <tt>MongoClient</tt> please see com.mongodb.MongoClient docs
   * </p>
   *
   * @param mongoClient           database connection client
   * @param databaseName          database name
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see MongoClient
   */
  public SpringMongockBuilder(MongoClient mongoClient, String databaseName, String changeLogsScanPackage) {
    super(mongoClient, databaseName, changeLogsScanPackage);
  }

  /**
   * Sets pre-configured {@link MongoTemplate} instance to use by the changelogs
   *
   * @param mongoTemplate instance of the {@link MongoTemplate}
   * @return Mongock builder
   */
  public SpringMongockBuilder setMongoTemplate(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
    return this;
  }

  /**
   * Set Environment object for Spring Profiles (@Profile) integration
   *
   * @param springEnvironment org.springframework.core.env.Environment object to inject
   * @return Mongock builder
   * @see org.springframework.context.annotation.Profile
   */
  public SpringMongockBuilder setSpringEnvironment(Environment springEnvironment) {
    this.springEnvironment = springEnvironment;
    return this;
  }

  public SpringMongock build() {
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

    SpringChangeService changeService = new SpringChangeService();
    changeService.setEnvironment(springEnvironment);
    changeService.setChangeLogsBasePackage(changeLogsScanPackage);

    final DB db = mongoClient.getDB(databaseName);
    return this.build(
        changeEntryRepository,
        changeService,
        lockChecker,
        proxyFactory.createProxyFromOriginal(mongoClient.getDatabase(databaseName)),
        proxyFactory.createProxyFromOriginal(db),
        proxyFactory.createProxyFromOriginal(mongoTemplate != null ? mongoTemplate : new MongoTemplate(mongoClient, databaseName))
    );
  }

  SpringMongock build(ChangeEntryRepository changeEntryRepository,
                ChangeService changeService,
                LockChecker lockChecker,
                MongoDatabase mongoDatabaseProxy,
                DB dbProxy,
                MongoTemplate mongoTemplateProxy) {
    SpringMongock mongock = new SpringMongock(changeEntryRepository, mongoClient,  changeService, lockChecker);
    mongock.setChangelogMongoDatabase(mongoDatabaseProxy);
    mongock.setChangelogDb(dbProxy);
    mongock.setMongoTemplate(mongoTemplateProxy);
    mongock.setEnabled(enabled);
    mongock.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    return mongock;
  }
}
