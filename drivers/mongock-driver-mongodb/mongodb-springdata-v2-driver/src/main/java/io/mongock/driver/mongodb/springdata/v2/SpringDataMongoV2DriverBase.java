package io.mongock.driver.mongodb.springdata.v2;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.impl.MongockTemplate;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.driver.ChangeSetDependency;
import io.mongock.driver.api.driver.ChangeSetDependencyBuildable;
import io.mongock.driver.api.driver.Transactional;
import io.mongock.driver.api.entry.ChangeEntryService;
import io.mongock.driver.mongodb.v3.driver.MongoCore3DriverGeneric;
import io.mongock.utils.annotation.NotThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.SessionSynchronization;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Optional;

@NotThreadSafe
public  class SpringDataMongoV2DriverBase extends MongoCore3DriverGeneric {

  protected static final Logger logger = LoggerFactory.getLogger(SpringDataMongoV2DriverBase.class);

  protected final MongoTemplate mongoTemplate;
  protected PlatformTransactionManager txManager;

  protected SpringDataMongoV2DriverBase(MongoTemplate mongoTemplate,
                                    long lockAcquiredForMillis,
                                    long lockQuitTryingAfterMillis,
                                    long lockTryFrequencyMillis) {
    super(mongoTemplate.getDb(), lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
    this.mongoTemplate = mongoTemplate;
    disableTransaction();
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
    dependencies.add(new ChangeSetDependencyBuildable(
        MongockTemplate.class,
        MongoTemplate.class,
        impl -> new MongockTemplate((MongoTemplate) impl),
        true));
    dependencies.add(new ChangeSetDependency(MongoTemplate.class, this.mongoTemplate));

    txManager = new MongoTransactionManager(mongoTemplate.getMongoDbFactory(), txOptions);
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
  public ChangeEntryService getChangeEntryService() {
    if (changeEntryRepository == null) {
      changeEntryRepository = new SpringDataMongoV2ChangeEntryRepository(mongoTemplate, getMigrationRepositoryName(), getReadWriteConfiguration(), isTransactionable());
      changeEntryRepository.setIndexCreation(isIndexCreation());
    }
    return changeEntryRepository;
  }

  @Override
  public Optional<Transactional> getTransactioner() {
    return Optional.ofNullable(transactionEnabled ? this : null);
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

  protected TransactionStatus getTxStatus(PlatformTransactionManager txManager) {
    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
// explicitly setting the transaction name is something that can be done only programmatically
    def.setName("mongock-transaction-spring-data-2");
    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    return txManager.getTransaction(def);
  }


  @Deprecated
  public void enableTransactionWithTxManager(PlatformTransactionManager txManager) {
    enableTransaction();
  }
}
