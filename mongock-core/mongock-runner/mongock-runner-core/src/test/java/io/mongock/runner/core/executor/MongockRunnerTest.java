package io.mongock.runner.core.executor;

import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.lock.LockCheckException;
import io.mongock.runner.core.event.EventPublisher;
import io.mongock.runner.core.event.result.MigrationSuccessResult;
import io.mongock.runner.core.executor.changelog.ChangeLogService;
import io.mongock.runner.core.executor.operation.migrate.MigrateAllExecutor;
import org.junit.jupiter.api.Test;
import org.mockito.internal.verification.Times;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MongockRunnerTest {

  @Test
  public void shouldNotBeExecutedNorEventPublished_IfDisabled() {

    Executor executor = mock(MigrateAllExecutor.class);

    EventPublisher eventPublisher = mock(EventPublisher.class);
    new MongockRunnerImpl(executor, true, false, eventPublisher).execute();

    verify(executor, new Times(0)).executeMigration();
    verify(eventPublisher, new Times(0)).publishMigrationSuccessEvent(new MigrationSuccessResult(true));
    verify(eventPublisher, new Times(0)).publishMigrationFailedEvent(any());
  }


  @Test
  public void shouldPropagateException_IfFExecuteMigrationFails() {

    Executor executor = mock(MigrateAllExecutor.class);

    doThrow(MongockException.class).when(executor)
        .executeMigration();

    EventPublisher eventPublisher = mock(EventPublisher.class);
    assertThrows(MongockException.class, () -> new MongockRunnerImpl(executor, true, true, eventPublisher).execute());

    verify(eventPublisher, new Times(0)).publishMigrationSuccessEvent(new MigrationSuccessResult(true));
    verify(eventPublisher, new Times(1)).publishMigrationFailedEvent(any());
  }


  @Test
  public void shouldPropagateMongockException_EvenWhenThrowExIfCannotLock_IfMigrationExecutionFails() {

    Executor executor = mock(MigrateAllExecutor.class);

    doThrow(MongockException.class).when(executor)
        .executeMigration();

    assertThrows(MongockException.class,
            () -> new MongockRunnerImpl(executor, false, true, mock(EventPublisher.class)).execute());

  }

  @Test
  public void shouldPropagateLockExceptionWrappedInMongockException_whenExecuteMigrationFails_IfThrowExceptionIfCannotObtainLockTrue() {

    Executor executor = mock(MigrateAllExecutor.class);

    doThrow(LockCheckException.class).when(executor)
        .executeMigration();

    assertThrows(MongockException.class,
            () -> new MongockRunnerImpl(executor, true, true, mock(EventPublisher.class)).execute());

  }

  @Test
  public void shouldNotPropagateLockException_whenExecuteMigrationFails_IfThrowExceptionIfCannotObtainLockFalse() {

    Executor executor = mock(MigrateAllExecutor.class);

    doThrow(LockCheckException.class).when(executor)
        .executeMigration();

    new MongockRunnerImpl(executor, false, true, mock(EventPublisher.class)).execute();

  }


  @Test
  public void shouldWrapGenericExceptionInMongockException() {

    Executor executor = mock(MigrateAllExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(RuntimeException.class).when(executor)
        .executeMigration();

    assertThrows(MongockException.class,
                  () -> new MongockRunnerImpl(executor, false, true, mock(EventPublisher.class)).execute());

  }


  //Events
  @Test
  public void shouldPublishSuccessEvent_whenMigrationSucceed() {

    Executor executor = mock(MigrateAllExecutor.class);

    EventPublisher eventPublisher = mock(EventPublisher.class);

    new MongockRunnerImpl(executor, true, true, eventPublisher)
        .execute();
  }


  @Test
  public void shouldPropagateMongockException_EvenWhenThrowExIfCannotLock_IfChangelogServiceNotValidated() {

    Executor executor = new Executor() {
      @Override
      public Object executeMigration() {
        throw new LockCheckException("Cannot obtain lock");
      }

      @Override
      public boolean isExecutionInProgress() {
        return false;
      }
    };

    MongockException ex = assertThrows(MongockException.class,
                  () -> new MongockRunnerImpl(executor, true, true, mock(EventPublisher.class)).execute());
    assertEquals("Cannot obtain lock", ex.getCause().getMessage());

  }


}
