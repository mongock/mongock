package io.mongock.runner.standalone.base;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.runner.core.builder.BuilderType;
import io.mongock.runner.core.builder.RunnerBuilderBase;
import io.mongock.runner.core.event.EventPublisher;
import io.mongock.runner.core.event.MigrationFailureEvent;
import io.mongock.runner.core.event.MigrationStartedEvent;
import io.mongock.runner.core.event.MigrationSuccessEvent;
import io.mongock.runner.core.executor.ExecutorBuilder;
import io.mongock.runner.core.executor.changelog.ChangeLogServiceBase;
import io.mongock.runner.core.executor.dependency.DependencyManager;

import java.util.function.Consumer;

public abstract class StandaloneBuilderBase<
    SELF extends StandaloneBuilderBase<SELF, CONFIG>,
    CONFIG extends MongockConfiguration>
    extends RunnerBuilderBase<SELF, CONFIG> {

  protected StandaloneBuilderBase(BuilderType builderType,
                                  ExecutorBuilder<CONFIG> executorFactory,
                                  ChangeLogServiceBase changeLogService,
                                  CONFIG config) {
    super(builderType, executorFactory, changeLogService, new DependencyManager(), config);
  }

  public SELF setMigrationStartedListener(Consumer<MigrationStartedEvent> listener) {
    this.eventPublisher = new EventPublisher(
        () -> listener.accept(new MigrationStartedEvent()),
        eventPublisher.getMigrationSuccessListener(),
        eventPublisher.getMigrationFailedListener());
    return getInstance();
  }

  public SELF setMigrationSuccessListener(Consumer<MigrationSuccessEvent> listener) {
    this.eventPublisher = new EventPublisher(
        eventPublisher.getMigrationStartedListener(),
        result -> listener.accept(new MigrationSuccessEvent(result)),
        eventPublisher.getMigrationFailedListener());
    return getInstance();
  }

  public SELF setMigrationFailureListener(Consumer<MigrationFailureEvent> listener) {
    this.eventPublisher = new EventPublisher(
        eventPublisher.getMigrationStartedListener(),
        eventPublisher.getMigrationSuccessListener(),
        result -> listener.accept(new MigrationFailureEvent(result)));
    return getInstance();
  }


}
