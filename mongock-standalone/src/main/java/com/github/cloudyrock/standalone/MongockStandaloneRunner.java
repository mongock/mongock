package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.MongockAnnotationProcessor;
import com.github.cloudyrock.mongock.MongockConnectionDriver;
import io.changock.runner.core.ChangeLogService;
import io.changock.runner.core.ChangockBase;
import io.changock.runner.core.MigrationExecutor;
import io.changock.runner.core.builder.DriverBuilderConfigurable;
import io.changock.runner.core.builder.RunnerBuilderBase;

/**
 * Mongock runner
 *
 * @since 26/07/2014
 */
public class MongockStandaloneRunner extends ChangockBase {


  public static DriverBuilderConfigurable<Builder, MongockConnectionDriver> builder() {
    return new Builder();
  }


  private MongockStandaloneRunner(MigrationExecutor executor,
                                  ChangeLogService changeLogService,
                                  boolean throwExceptionIfCannotObtainLock,
                                  boolean enabled) {
    super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled);
  }


  public static class Builder extends RunnerBuilderBase<Builder, MongockConnectionDriver> {

    private Builder() {
      this.overrideAnnoatationProcessor(new MongockAnnotationProcessor());
    }

    public MongockStandaloneRunner build() {
      return new MongockStandaloneRunner(buildExecutorDefault(), buildChangeLogServiceDefault(), throwExceptionIfCannotObtainLock, enabled);
    }

    @Override
    protected Builder returnInstance() {
      return this;
    }
  }

}
