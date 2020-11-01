package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.MongockAnnotationProcessor;
import com.github.cloudyrock.mongock.MongockConnectionDriver;
import io.changock.runner.core.ChangeLogService;
import io.changock.runner.core.EventPublisher;
import io.changock.runner.core.MigrationExecutor;
import io.changock.runner.core.builder.configuration.ChangockConfiguration;
import io.changock.runner.core.builder.DriverBuilderConfigurable;
import io.changock.runner.core.builder.RunnerBuilderBase;
import io.changock.runner.standalone.ChangockStandalone;

/**
 * Mongock runner
 *
 * @since 26/07/2014
 */
public class MongockStandalone {


  public static DriverBuilderConfigurable<Builder, MongockConnectionDriver, ChangockConfiguration> builder() {
    return new Builder();
  }


  public static class Builder extends RunnerBuilderBase<Builder, MongockConnectionDriver, ChangockConfiguration> {

    private Builder() {
      this.overrideAnnoatationProcessor(new MongockAnnotationProcessor());
    }

    public Runner buildRunner() {
      return new Runner(buildExecutorDefault(), buildChangeLogServiceDefault(), throwExceptionIfCannotObtainLock, enabled);
    }

    @Override
    protected Builder returnInstance() {
      return this;
    }
  }

  public static class Runner extends ChangockStandalone.ChangockStandaloneRunner {


    private Runner(MigrationExecutor executor,
                   ChangeLogService changeLogService,
                   boolean throwExceptionIfCannotObtainLock,
                   boolean enabled) {
      super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled,
          new EventPublisher() {
            @Override
            public void publishMigrationSuccessEvent() {

            }

            @Override
            public void publishMigrationFailedEvent(Exception ex) {

            }
          }

      );
    }
  }
}
