package io.mongock.runner.core.event;

import io.mongock.runner.core.event.result.MigrationSuccessResult;

import java.util.function.Consumer;

public class EventPublisher {

  private final Runnable migrationStartedListener;
  private final Consumer<MigrationSuccessResult> migrationSuccessListener;
  private final Consumer<Exception> migrationFailedListener;


  public EventPublisher() {
    this(null, null, null);
  }

  public EventPublisher(Runnable migrationStartedListener,
                        Consumer<MigrationSuccessResult> migrationSuccessListener,
                        Consumer<Exception> migrationFailedListener) {
    this.migrationSuccessListener = migrationSuccessListener;
    this.migrationFailedListener = migrationFailedListener;
    this.migrationStartedListener = migrationStartedListener;
  }

  public void publishMigrationStarted() {
    if (migrationStartedListener != null) {
      migrationStartedListener.run();
    }
  }

  public void publishMigrationSuccessEvent(MigrationSuccessResult migrationResult) {
    if (migrationSuccessListener != null) {
      migrationSuccessListener.accept(migrationResult);
    }
  }

  public void publishMigrationFailedEvent(Exception ex) {
    if (migrationFailedListener != null) {
      migrationFailedListener.accept(ex);
    }
  }

  public Runnable getMigrationStartedListener() {
    return migrationStartedListener;
  }

  public Consumer<MigrationSuccessResult> getMigrationSuccessListener() {
    return migrationSuccessListener;
  }

  public Consumer<Exception> getMigrationFailedListener() {
    return migrationFailedListener;
  }
}
