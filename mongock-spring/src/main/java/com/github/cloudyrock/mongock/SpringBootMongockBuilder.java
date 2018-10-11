package com.github.cloudyrock.mongock;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * Factory for {@link SpringBootMongock}
 */
public class SpringBootMongockBuilder extends MongockBuilder {
  private ApplicationContext context;

  /**
   * <p>Builder constructor takes db.mongodb.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about <tt>MongoClient</tt> please see com.mongodb.MongoClient docs
   * </p>
   *
   * @param mongoClient database connection client
   * @param databaseName database name
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see MongoClient
   */
  public SpringBootMongockBuilder(MongoClient mongoClient, String databaseName, String changeLogsScanPackage) {
    super(mongoClient, databaseName, changeLogsScanPackage);
  }

  public SpringBootMongockBuilder setApplicationContext(ApplicationContext context) {
    this.context = context;
    return this;
  }

  public SpringBootMongock build() {
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

    final Set<String> proxyCreatorAndUncheckedmethods = new HashSet<>(
        Arrays.asList("getCollection", "getCollectionFromString", "getDatabase", "toString"));

    ProxyFactory proxyFactory =
        new ProxyFactory(preInterceptor, proxyCreatorAndUncheckedmethods, proxyCreatorAndUncheckedmethods);

    //ChangeService
    ChangeEntryRepository changeEntryRepository = new ChangeEntryRepository(changeLogCollectionName, database);
    changeEntryRepository.ensureIndex();

    SpringChangeService changeService = new SpringChangeService();
    changeService.setEnvironment(context.getBean(Environment.class));
    changeService.setChangeLogsBasePackage(changeLogsScanPackage);

    final DB db = mongoClient.getDB(databaseName);
    return this.build(
        changeEntryRepository,
        changeService,
        lockChecker,
        proxyFactory.createProxyFromOriginal(mongoClient.getDatabase(databaseName)),
        proxyFactory.createProxyFromOriginal(db),
        context);
  }

  private SpringBootMongock build(ChangeEntryRepository changeEntryRepository,
      ChangeService changeService,
      LockChecker lockChecker,
      MongoDatabase mongoDatabaseProxy,
      DB dbProxy,
      ApplicationContext context) {
    SpringBootMongock mongock = new SpringBootMongock(changeEntryRepository, mongoClient, changeService, lockChecker);
    mongock.setChangelogMongoDatabase(mongoDatabaseProxy);
    mongock.setChangelogDb(dbProxy);
    mongock.setEnabled(enabled);
    mongock.springContext(context);
    mongock.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    return mongock;
  }

  @Override
  void validateMandatoryFields() throws MongockException {
    super.validateMandatoryFields();
    if (context == null) {
      throw new MongockException("ApplicationContext must be set to use HDFMongockBuilder");
    }
  }

}
