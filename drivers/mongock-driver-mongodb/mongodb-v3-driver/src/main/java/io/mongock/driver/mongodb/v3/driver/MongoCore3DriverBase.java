package io.mongock.driver.mongodb.v3.driver;

import io.mongock.driver.api.driver.Transactionable;
import io.mongock.api.exception.MongockException;
import io.mongock.utils.annotation.NotThreadSafe;
import com.mongodb.MongoClientException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.TransactionBody;

import java.util.Optional;

@NotThreadSafe
public  abstract class MongoCore3DriverBase extends MongoCore3DriverGeneric {

  private final MongoClient mongoClient;
  protected ClientSession clientSession;

  protected MongoCore3DriverBase(MongoClient mongoClient,
                             String databaseName,
                             long lockAcquiredForMillis,
                             long lockQuitTryingAfterMillis,
                             long lockTryFrequencyMillis) {
    super(mongoClient.getDatabase(databaseName), lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
    this.mongoClient = mongoClient;
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
  public Optional<Transactionable> getTransactioner() {
    return Optional.ofNullable(transactionEnabled ? this : null);
  }
}
