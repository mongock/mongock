package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.driver;

import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.driver.api.driver.Transactioner;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.utils.annotation.NotThreadSafe;
import com.mongodb.MongoClientException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.TransactionBody;

import java.util.Optional;
import java.util.Set;

@NotThreadSafe
public abstract class MongoSync4DriverBase<CHANGE_ENTRY extends ChangeEntry> extends MongoSync4DriverGeneric<CHANGE_ENTRY> {

  private final MongoClient mongoClient;
  protected ClientSession clientSession;
  private boolean transactionEnabled = true;

  protected MongoSync4DriverBase(MongoClient mongoClient,
                             String databaseName,
                             long lockAcquiredForMillis,
                             long lockQuitTryingAfterMillis,
                             long lockTryFrequencyMillis) {
    super(mongoClient.getDatabase(databaseName), lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
    this.mongoClient = mongoClient;
  }

  //todo change this for prepareForMigrationBlock, which can be a changeLog(default) or the entire migration
  //todo reflects it in the MigrationExecutor
  @Override
  public void prepareForChangelogExecution() {
    try {
      clientSession = mongoClient.startSession();
    } catch (MongoClientException ex) {
      throw new MongockException("ERROR starting session. If Mongock is connected to a MongoDB cluster which doesn't support transactions, you must to disable transactions", ex);
    }
  }

  @Override
  public Set<ChangeSetDependency> getDependencies() {
    Set<ChangeSetDependency> dependencies = super.getDependencies();
    if(clientSession != null) {
      dependencies.add(new ChangeSetDependency(ClientSession.class, clientSession));
    }
    return dependencies;
  }

  @Override
  public void executeInTransaction(Runnable operation) {
    try {
      clientSession.withTransaction(getTransactionBody(operation), txOptions);
    } catch (Exception ex) {
      throw new MongockException(ex);
    } finally {
      clientSession.close();
    }
  }

  private TransactionBody<String> getTransactionBody(Runnable operation) {
    return () -> {
      operation.run();
      return "Mongock transaction operation";
    };
  }

  public void disableTransaction() {
    transactionEnabled = false;
  }

  @Override
  public Optional<Transactioner> getTransactioner() {
    return Optional.ofNullable(transactionEnabled ? this : null);
  }
}
