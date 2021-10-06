package io.mongock.runner.spring.base.events;

import io.mongock.runner.core.event.MongockResultEvent;
import io.mongock.runner.core.event.result.MigrationResult;
import io.mongock.runner.core.event.result.MigrationSuccessResult;
import org.springframework.context.ApplicationEvent;

public class SpringMigrationSuccessEvent extends ApplicationEvent implements MongockResultEvent {

  private final MigrationSuccessResult migrationResult;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public SpringMigrationSuccessEvent(Object source, MigrationSuccessResult migrationResult) {
    super(source);
    this.migrationResult = migrationResult;
  }

  @Override
  public MigrationResult getMigrationResult() {
    return migrationResult;
  }

  @Override
  public String toString() {
    return "SpringMigrationSuccessEvent{" +
        "migrationResult=" + migrationResult +
        "} " + super.toString();
  }
}
