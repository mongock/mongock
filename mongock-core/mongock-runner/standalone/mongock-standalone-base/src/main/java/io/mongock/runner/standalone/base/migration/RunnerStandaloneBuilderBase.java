package io.mongock.runner.standalone.base.migration;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.event.MigrationFailureEvent;
import io.mongock.runner.core.event.MigrationStartedEvent;
import io.mongock.runner.core.event.MigrationSuccessEvent;

import java.util.function.Consumer;

public interface RunnerStandaloneBuilderBase<
    SELF extends RunnerStandaloneBuilderBase<SELF, CONFIG>,
    
    CONFIG extends MongockConfiguration>
    extends RunnerBuilder<SELF, CONFIG> {

  //TODO javadoc
  SELF setMigrationStartedListener(Consumer<MigrationStartedEvent> listener);

  //TODO javadoc
  SELF setMigrationSuccessListener(Consumer<MigrationSuccessEvent> listener);

  //TODO javadoc
  SELF setMigrationFailureListener(Consumer<MigrationFailureEvent> listener);
}
