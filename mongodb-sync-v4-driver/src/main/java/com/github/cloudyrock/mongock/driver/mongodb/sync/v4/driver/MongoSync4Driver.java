package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.driver;

import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.utils.TimeService;
import com.github.cloudyrock.mongock.utils.annotation.NotThreadSafe;
import com.mongodb.MongoClientException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.TransactionBody;

import static com.github.cloudyrock.mongock.TransactionStrategy.MIGRATION;

@NotThreadSafe
public class MongoSync4Driver extends MongoSync4DriverBase<ChangeEntry> {

  private MongoClient mongoClient;

  protected MongoSync4Driver(MongoClient mongoClient,
                             String databaseName,
                             long lockAcquiredForMillis,
                             long lockQuitTryingAfterMillis,
                             long lockTryFrequencyMillis) {
    super(mongoClient.getDatabase(databaseName), lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
    this.mongoClient = mongoClient;
    setTransactionStrategy(MIGRATION);
  }

  @Override
  public void executeInTransaction(Runnable operation) {
    ClientSession clientSession;
    try {
      clientSession = mongoClient.startSession();
    } catch (MongoClientException ex) {
      throw new MongockException("ERROR starting session. If Mongock is connected to a MongoDB cluster which doesn't support transactions, you must to disable transactions", ex);
    }
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

  ////////////////////////////////////////////////////////////
  //BUILDER METHODS
  ////////////////////////////////////////////////////////////


  //TODO CENTRALIZE DEFAULT PROPERTIES
  public static MongoSync4Driver withDefaultLock(MongoClient mongoClient, String databaseName) {
    return MongoSync4Driver.withLockStrategy(mongoClient, databaseName, 60 * 1000L, 3 * 60 * 1000L, 1000L);
  }

  public static MongoSync4Driver withLockStrategy(MongoClient mongoClient,
                                                  String databaseName,
                                                  long lockAcquiredForMillis,
                                                  long lockQuitTryingAfterMillis,
                                                  long lockTryFrequencyMillis) {
    return new MongoSync4Driver(mongoClient, databaseName, lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
  }


  /**
   * @Deprecated Use withLockStrategy instead
   */
  @Deprecated
  public static MongoSync4Driver withLockSetting(MongoClient mongoClient,
                                                 String databaseName,
                                                 long lockAcquiredForMinutes,
                                                 long maxWaitingForLockMinutes,
                                                 int maxTries) {
    TimeService timeService = new TimeService();
    long lockAcquiredForMillis = timeService.minutesToMillis(lockAcquiredForMinutes);
    long lockQuitTryingAfterMillis = timeService.minutesToMillis(maxWaitingForLockMinutes * maxTries);
    long tryFrequency = 1000L;// 1 second
    return MongoSync4Driver.withLockStrategy(mongoClient, databaseName, lockAcquiredForMillis, lockQuitTryingAfterMillis, tryFrequency);
  }
}
