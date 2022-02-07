package io.mongock.driver.mongodb.reactive.util;

import org.reactivestreams.Subscription;

import java.util.Optional;

public interface SubscriberSync<T> extends org.reactivestreams.Subscriber<T> {

  default T getFirst() {
    return get().get(0);
  }

  MongoIterable<T> get(long timeoutMS);

  Optional<Throwable> getError();

  Subscription getSubscription();

  MongoSubscriberSync<T> await(long timeout);

  boolean isCompleted();

  default boolean isFailed() {
    return isCompleted() && getError() != null;
  }

  default MongoIterable<T> get() {
    return get(Long.MAX_VALUE);
  }

  default MongoSubscriberSync<T> await() {
    return await(Long.MAX_VALUE);
  }

}
