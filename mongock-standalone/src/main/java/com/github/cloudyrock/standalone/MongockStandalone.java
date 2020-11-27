package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.MongockAnnotationProcessor;
import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.migration.api.config.ChangockConfiguration;
import io.changock.runner.core.builder.DriverBuilderConfigurable;
import io.changock.runner.core.event.EventPublisher;
import io.changock.runner.core.executor.ChangeLogService;
import io.changock.runner.core.executor.MigrationExecutor;
import io.changock.runner.standalone.StandaloneBuilderBase;
import io.changock.runner.standalone.StandaloneRunner;

/**
 * Mongock runner
 *
 * Deprecated. Use ChangockStandalone instead
 *
 * @see io.changock.runner.standalone.ChangockStandalone
 *
 * @since 26/07/2020
 */
@Deprecated
public class MongockStandalone {


  public static DriverBuilderConfigurable<Builder, ConnectionDriver, ChangockConfiguration> builder() {
    return new Builder();
  }


  public static class Builder extends StandaloneBuilderBase<Builder, ConnectionDriver> {

    private Builder() {
      this.overrideAnnoatationProcessor(new MongockAnnotationProcessor());
    }

    public Runner buildRunner() {
      return new Runner(buildExecutorDefault(), buildChangeLogServiceDefault(), throwExceptionIfCannotObtainLock, enabled, getEventPublisher());
    }

    @Override
    protected Builder returnInstance() {
      return this;
    }
  }

  public static class Runner extends StandaloneRunner {

    private Runner(MigrationExecutor executor,
                   ChangeLogService changeLogService,
                   boolean throwExceptionIfCannotObtainLock,
                   boolean enabled,
                   EventPublisher eventPublisher) {
      super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
    }
  }

}
