package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.MongockAnnotationProcessor;
import com.github.cloudyrock.mongock.MongockConnectionDriver;
import io.changock.runner.core.ChangeLogService;
import io.changock.runner.core.EventPublisher;
import io.changock.runner.core.MigrationExecutor;
import io.changock.runner.core.builder.DriverBuilderConfigurable;
import io.changock.runner.core.builder.configuration.ChangockConfiguration;
import io.changock.runner.standalone.StandaloneBuilder;
import io.changock.runner.standalone.StandaloneRunner;

/**
 * Mongock runner
 *
 * @since 26/07/2020
 */
public class MongockStandalone {


  public static DriverBuilderConfigurable<Builder, MongockConnectionDriver, ChangockConfiguration> builder() {
    return new Builder();
  }


  public static class Builder extends StandaloneBuilder<Builder, MongockConnectionDriver> {

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
