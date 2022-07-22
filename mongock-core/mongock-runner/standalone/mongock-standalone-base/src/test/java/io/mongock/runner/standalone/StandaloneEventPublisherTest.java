package io.mongock.runner.standalone;

import io.mongock.runner.core.event.EventPublisher;
import io.mongock.runner.core.event.result.MigrationSuccessResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class StandaloneEventPublisherTest {

  @Test
  public void shouldCallStartedListener() {
    Listener listener = new Listener();
    new EventPublisher(listener::startedListener, listener::successListener, listener::failListener).publishMigrationStarted();
    assertTrue(listener.isStartedCalled());
    assertFalse(listener.isSuccessCalled());
    assertFalse(listener.isFailCalled());
  }

  @Test
  public void shouldCallSuccessListener() {
    Listener listener = new Listener();
    new EventPublisher(listener::startedListener, listener::successListener, listener::failListener).publishMigrationSuccessEvent(new MigrationSuccessResult(true));
    assertFalse(listener.isStartedCalled());
    assertTrue(listener.isSuccessCalled());
    assertFalse(listener.isFailCalled());
  }


  @Test
  public void shouldCallFailListener() {
    Listener listener = new Listener();
    RuntimeException ex = new RuntimeException();
    new EventPublisher(listener::startedListener, listener::successListener, listener::failListener).publishMigrationFailedEvent(ex);
    assertFalse(listener.isStartedCalled());
    assertFalse(listener.isSuccessCalled());
    assertTrue(listener.isFailCalled());
    assertEquals(ex, listener.getException());
  }

  @Test
  public void shouldNotBreak_WhenSuccess_ifListenerIsNull() {
    new EventPublisher(null, null, null).publishMigrationSuccessEvent(new MigrationSuccessResult(true));
  }

  @Test
  public void shouldNotBreak_WhenFail_ifListenerIsNull() {
    new EventPublisher(null, null, null).publishMigrationFailedEvent(new Exception());
  }

}


class Listener {

  private boolean startedCalled = false;
  private boolean successCalled = false;
  private boolean failCalled = false;
  private Exception exception;

  void startedListener() {
    startedCalled = true;
  }

  void successListener(MigrationSuccessResult successEvent) {
    successCalled = true;
  }

  void failListener(Exception exception) {
    failCalled = true;
    this.exception = exception;
  }

  boolean isStartedCalled() {
    return startedCalled;
  }

  boolean isSuccessCalled() {
    return successCalled;
  }

  boolean isFailCalled() {
    return failCalled;
  }

  Exception getException() {
    return exception;
  }


}
