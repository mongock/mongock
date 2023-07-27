package io.mongock.driver.mongodb.v3.driver;

import com.mongodb.MongoClientException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.TransactionBody;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.driver.ChangeSetDependency;
import io.mongock.driver.api.driver.Transactional;
import io.mongock.utils.annotation.NotThreadSafe;

import java.util.Optional;
import java.util.Set;

@NotThreadSafe
public abstract class MongoCore3DriverBase extends MongoCore3DriverGeneric {

  private final MongoClient mongoClient;
  private final String databaseName;
  protected ClientSession clientSession;

  protected MongoCore3DriverBase(MongoClient mongoClient,
                                 String databaseName,
                                 long lockAcquiredForMillis,
                                 long lockQuitTryingAfterMillis,
                                 long lockTryFrequencyMillis) {
    super(lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
    this.mongoClient = mongoClient;
    this.databaseName = databaseName;
  }

  @Override
  protected MongoDatabase getDataBase() {
    return mongoClient.getDatabase(databaseName);
  }

  @Override
  public void prepareForExecutionBlock() {
    try {
      clientSession = mongoClient.startSession();
    } catch (MongoClientException ex) {
      throw new MongockException("ERROR starting session. If Mongock is connected to a MongoDB cluster which doesn't support transactions, you must to disable transactions", ex);
    }
  }
  
  @Override
  public Set<ChangeSetDependency> getDependencies() {
    Set<ChangeSetDependency> dependencies = super.getDependencies();
    if (clientSession != null) {
      ChangeSetDependency clientSessionDependency = new ChangeSetDependency(ClientSession.class, clientSession, false);
      dependencies.remove(clientSessionDependency);
      dependencies.add(clientSessionDependency);
    }

    return dependencies;
  }

  @Override
  public void executeInTransaction(Runnable operation) {
    try {
      changeEntryRepository.setClientSession(clientSession);
      clientSession.withTransaction(getTransactionBody(operation), txOptions);
    } catch (Exception ex) {
      throw new MongockException(ex);
    } finally {
      changeEntryRepository.clearClientSession();
      clientSession.close();
    }
  }

  private TransactionBody<String> getTransactionBody(Runnable operation) {
    return () -> {
      operation.run();
      return "Mongock transaction operation";
    };
  }

  @Override
  public Optional<Transactional> getTransactioner() {
    return Optional.ofNullable(transactionEnabled ? this : null);
  }
}
