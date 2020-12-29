package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.impl.MongockTemplate;
import com.github.cloudyrock.mongock.driver.mongodb.v3.driver.MongoCore3Driver;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.driver.api.driver.ForbiddenParametersMap;
import com.github.cloudyrock.mongock.driver.api.driver.TransactionStrategy;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntryService;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvokerImpl;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.utils.annotation.NotThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.SessionSynchronization;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@NotThreadSafe
public class SpringDataMongoV2Driver extends MongoCore3Driver {

  private static final Logger logger = LoggerFactory.getLogger(SpringDataMongoV2Driver.class);
  private static final ForbiddenParametersMap FORBIDDEN_PARAMETERS_MAP;

  static {
    FORBIDDEN_PARAMETERS_MAP = new ForbiddenParametersMap();
    FORBIDDEN_PARAMETERS_MAP.put(MongoTemplate.class, MongockTemplate.class);
  }

  private final MongoTemplate mongoTemplate;
  private MongoTransactionManager txManager;

  public static SpringDataMongoV2Driver withDefaultLock(MongoTemplate mongoTemplate) {
    return new SpringDataMongoV2Driver(mongoTemplate, 3L, 4L, 3);
  }

  public static SpringDataMongoV2Driver withLockSetting(MongoTemplate mongoTemplate,
                                                        long lockAcquiredForMinutes,
                                                        long maxWaitingForLockMinutes,
                                                        int maxTries) {
    return new SpringDataMongoV2Driver(mongoTemplate, lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries);
  }

  protected SpringDataMongoV2Driver(MongoTemplate mongoTemplate,
                                    long lockAcquiredForMinutes,
                                    long maxWaitingForLockMinutes,
                                    int maxTries) {
    super(mongoTemplate.getDb(), lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries);
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public void runValidation() throws MongockException {
    super.runValidation();

    if (this.mongoTemplate == null) {
      throw new MongockException("MongoTemplate must not be null");
    }
  }

  @Override
  public void specificInitialization() {
    super.specificInitialization();
    dependencies.add(new ChangeSetDependency(MongockTemplate.class, new MongockTemplate(mongoTemplate, new LockGuardInvokerImpl(this.getLockManager()))));
  }

  @Override
  public ForbiddenParametersMap getForbiddenParameters() {
    return FORBIDDEN_PARAMETERS_MAP;
  }

  public MongockTemplate getMongockTemplate() {
    if (!isInitialized()) {
      throw new MongockException("Mongock Driver hasn't been initialized yet");
    }
    return dependencies
        .stream()
        .filter(dependency -> MongockTemplate.class.isAssignableFrom(dependency.getType()))
        .map(ChangeSetDependency::getInstance)
        .map(instance -> (MongockTemplate) instance)
        .findAny()
        .orElseThrow(() -> new MongockException("Mongock Driver hasn't been initialized yet"));
  }

  @Override
  public ChangeEntryService<ChangeEntry> getChangeEntryService() {
    if (changeEntryRepository == null) {
      this.changeEntryRepository = new SpringDataMongoV2ChangeEntryRepository<>(mongoTemplate, changeLogCollectionName, indexCreation);
    }
    return changeEntryRepository;
  }

  public void enableTransactionWithTxManager(MongoTransactionManager txManager) {
    this.txManager = txManager;
    this.transactionStrategy = TransactionStrategy.MIGRATION;
  }

  @Override
  public void executeInTransaction(Runnable operation) {
    TransactionStatus txStatus = getTxStatus(txManager);
    try {
      mongoTemplate.setSessionSynchronization(SessionSynchronization.ALWAYS);
      operation.run();
      txManager.commit(txStatus);
    } catch (Exception ex) {
      logger.warn("Error in Mongock's transaction", ex);
      txManager.rollback(txStatus);
    }
  }

  private TransactionStatus getTxStatus(MongoTransactionManager txManager) {
    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
    def.setName("SomeTxName");
    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    return txManager.getTransaction(def);
  }
}
