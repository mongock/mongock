package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3;

import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.driver.api.driver.Transactioner;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntryService;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvokerImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.driver.MongoSync4DriverGeneric;
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

import java.util.Optional;

@NotThreadSafe
public abstract class SpringDataMongoV3DriverBase<CHANGE_ENTRY extends ChangeEntry> extends MongoSync4DriverGeneric<CHANGE_ENTRY> {

  protected static final Logger logger = LoggerFactory.getLogger(SpringDataMongoV3DriverBase.class);

  protected final MongoTemplate mongoTemplate;
  protected MongoTransactionManager txManager;

  protected SpringDataMongoV3DriverBase(MongoTemplate mongoTemplate,
                                    long lockAcquiredForMillis,
                                    long lockQuitTryingAfterMillis,
                                    long lockTryFrequencyMillis) {
    super(mongoTemplate.getDb(), lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
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
  public ChangeEntryService<CHANGE_ENTRY> getChangeEntryService() {
    if (changeEntryRepository == null) {
      this.changeEntryRepository = new SpringDataMongoV3ChangeEntryRepository<>(mongoTemplate, changeLogCollectionName, indexCreation, getReadWriteConfiguration());
    }
    return changeEntryRepository;
  }

  @Override
  public void disableTransaction() {
    this.txManager = null;
  }

  public void enableTransactionWithTxManager(MongoTransactionManager txManager) {
    this.txManager = txManager;
  }

  @Override
  public Optional<Transactioner> getTransactioner() {
    return Optional.ofNullable(txManager != null ? this : null);
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
      throw new MongockException(ex);
    }

  }

  protected TransactionStatus getTxStatus(MongoTransactionManager txManager) {
    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
// explicitly setting the transaction name is something that can be done only programmatically
    def.setName("mongock-transaction-spring-data-3");
    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    return txManager.getTransaction(def);
  }
}
