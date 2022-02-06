package io.mongock.driver.mongodb.reactive.driver;

import com.mongodb.MongoClientException;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoClient;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.driver.ChangeSetDependency;
import io.mongock.driver.api.driver.Transactional;
import io.mongock.driver.mongodb.reactive.util.MongoSubscriber;
import io.mongock.driver.mongodb.reactive.util.SubscriberSync;
import io.mongock.utils.annotation.NotThreadSafe;
import org.reactivestreams.Publisher;

import java.util.Optional;
import java.util.Set;

@NotThreadSafe
public abstract class MongoReactiveDriverBase extends MongoReactiveDriverGeneric {

  private final MongoClient mongoClient;
  protected ClientSession clientSession;

  protected MongoReactiveDriverBase(MongoClient mongoClient,
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
      MongoSubscriber<ClientSession> subscriber = new SubscriberSync<>();
      mongoClient.startSession().subscribe(subscriber);
      clientSession = subscriber.getFirst();
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

      //TODO confirm this is right
      clientSession.startTransaction(txOptions);
      operation.run();
      voidSubscribe(clientSession.commitTransaction());
    } catch (Exception ex) {
      voidSubscribe(clientSession.abortTransaction());
      throw new MongockException(ex);
    } finally {
      changeEntryRepository.clearClientSession();
      clientSession.close();
    }
  }

  private SubscriberSync<Void> voidSubscribe(Publisher<Void> voidPublisher) {
    MongoSubscriber<Void> subscriber = new SubscriberSync<>();
    voidPublisher.subscribe(subscriber);
    return subscriber.await();
  }


  @Override
  public Optional<Transactional> getTransactioner() {
    return Optional.ofNullable(transactionEnabled ? this : null);
  }
}
