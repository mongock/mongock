package io.mongock.driver.mongodb.reactive.util;

import io.mongock.api.exception.MongockException;
import org.reactivestreams.Subscription;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SubscriberSync<T> implements MongoSubscriber<T> {

  private final CountDownLatch latch = new CountDownLatch(1);
  private final MongoIterable<T> items = new MongoIterable<>();

  private Throwable error = null;
  private Subscription subscription;
  private boolean completed = false;


  @Override
  public void onSubscribe(Subscription subscription) {
    this.subscription = subscription;
  }

  @Override
  public void onNext(T t) {
    items.add(t);
  }

  @Override
  public void onError(Throwable error) {
    this.error = error;
    latch.countDown();
  }

  @Override
  public void onComplete() {
    this.completed = true;
    latch.countDown();
  }

  @Override
  public Optional<Throwable> getError() {
    return Optional.ofNullable(error);
  }

  @Override
  public Subscription getSubscription() {
    return subscription;
  }

  @Override
  public boolean isCompleted() {
    return completed;
  }

  @Override
  public MongoIterable<T> get(long timeoutMS) {
    return await(timeoutMS).items;
  }

  @Override
  public SubscriberSync<T> await(long timeout) {
    return await(timeout, true);
  }

  protected SubscriberSync<T> await(long timeout, boolean request) {
    if (request) {
      subscription.request(Long.MAX_VALUE);
    }
    boolean await;
    try {
      await = latch.await(timeout, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      throw new MongockException(e);
    }
    if (!await) {
      throw new MongockException(getClass().getSimpleName() + " publisher onComplete timed out");
    }
    if (error != null) {
      if (RuntimeException.class.isAssignableFrom(error.getClass())) {
        throw (RuntimeException) error;
      } else {
        throw new MongockException(error);
      }
    }
    return this;
  }
}
