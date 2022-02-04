package io.mongock.driver.mongodb.async.util;

import com.mongodb.CursorType;
import com.mongodb.ExplainVerbosity;
import com.mongodb.client.model.Collation;
import com.mongodb.reactivestreams.client.FindPublisher;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class FindPublisherMock<T> implements FindPublisher<T> {

  private final List<T> values;

  public FindPublisherMock(List<T> values) {
    this.values = values;
  }

  @Override
  public void subscribe(Subscriber<? super T> subscriber) {
    Subscription subscription = Mockito.mock(Subscription.class);
    subscriber.onSubscribe(subscription);
    values.forEach(subscriber::onNext);
    subscriber.onComplete();
  }


  @Override
  public Publisher<T> first() {
    return null;
  }

  @Override
  public FindPublisher<T> filter(Bson bson) {
    return null;
  }

  @Override
  public FindPublisher<T> limit(int i) {
    return null;
  }

  @Override
  public FindPublisher<T> skip(int i) {
    return null;
  }

  @Override
  public FindPublisher<T> maxTime(long l, TimeUnit timeUnit) {
    return null;
  }

  @Override
  public FindPublisher<T> maxAwaitTime(long l, TimeUnit timeUnit) {
    return null;
  }

  @Override
  public FindPublisher<T> projection(Bson bson) {
    return null;
  }

  @Override
  public FindPublisher<T> sort(Bson bson) {
    return null;
  }

  @Override
  public FindPublisher<T> noCursorTimeout(boolean b) {
    return null;
  }

  @Override
  public FindPublisher<T> oplogReplay(boolean b) {
    return null;
  }

  @Override
  public FindPublisher<T> partial(boolean b) {
    return null;
  }

  @Override
  public FindPublisher<T> cursorType(CursorType cursorType) {
    return null;
  }

  @Override
  public FindPublisher<T> collation(Collation collation) {
    return null;
  }

  @Override
  public FindPublisher<T> comment(String s) {
    return null;
  }

  @Override
  public FindPublisher<T> hint(Bson bson) {
    return null;
  }

  @Override
  public FindPublisher<T> hintString(String s) {
    return null;
  }

  @Override
  public FindPublisher<T> max(Bson bson) {
    return null;
  }

  @Override
  public FindPublisher<T> min(Bson bson) {
    return null;
  }

  @Override
  public FindPublisher<T> returnKey(boolean b) {
    return null;
  }

  @Override
  public FindPublisher<T> showRecordId(boolean b) {
    return null;
  }

  @Override
  public FindPublisher<T> batchSize(int i) {
    return null;
  }

  @Override
  public FindPublisher<T> allowDiskUse(Boolean aBoolean) {
    return null;
  }

  @Override
  public Publisher<Document> explain() {
    return null;
  }

  @Override
  public Publisher<Document> explain(ExplainVerbosity explainVerbosity) {
    return null;
  }

  @Override
  public <E> Publisher<E> explain(Class<E> aClass) {
    return null;
  }

  @Override
  public <E> Publisher<E> explain(Class<E> aClass, ExplainVerbosity explainVerbosity) {
    return null;
  }
}
