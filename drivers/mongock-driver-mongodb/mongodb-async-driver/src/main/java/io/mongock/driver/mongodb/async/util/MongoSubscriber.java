package io.mongock.driver.mongodb.async.util;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.Optional;

public interface MongoSubscriber<T> extends Subscriber<T> {

  default T getFirst() {
    return get().get(0);
  }

  MongoIterable<T> get(long timeoutMS);

  Optional<Throwable> getError();

  Subscription getSubscription();

  SubscriberSync<T> await(long timeout);

  boolean isCompleted();

  default boolean isFailed() {
    return isCompleted() && getError() != null;
  }

  default MongoIterable<T> get() {
    return get(Long.MAX_VALUE);
  }

  default SubscriberSync<T> await() {
    return await(Long.MAX_VALUE);
  }


}
