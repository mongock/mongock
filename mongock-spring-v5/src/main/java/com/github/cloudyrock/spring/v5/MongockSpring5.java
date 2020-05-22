package com.github.cloudyrock.spring.v5;

import com.github.cloudyrock.mongock.MongockAnnotationProcessor;
import com.github.cloudyrock.mongock.MongockConnectionDriver;
import io.changock.runner.core.builder.DriverBuilderConfigurable;
import io.changock.runner.spring.v5.ChangockSpringBuilderBase;
import io.changock.runner.spring.v5.core.ProfiledChangeLogService;
import io.changock.runner.spring.v5.core.SpringMigrationExecutor;

public class MongockSpring5 {
  public static DriverBuilderConfigurable<Builder, MongockConnectionDriver> builder() {
    return new Builder();
  }

  public static class Builder extends ChangockSpringBuilderBase<Builder, MongockConnectionDriver> {

    private Builder() {
      overrideAnnoatationProcessor(new MongockAnnotationProcessor());
    }

    public MongockApplicationRunner buildApplicationRunner() {
      return new MongockApplicationRunner(
          buildExecutorWithEnvironmentDependency(),
          buildProfiledChangeLogService(),
          throwExceptionIfCannotObtainLock,
          enabled);
    }

    public MongockInitializingBeanRunner buildInitializingBeanRunner() {
      return new MongockInitializingBeanRunner(
          buildExecutorWithEnvironmentDependency(),
          buildProfiledChangeLogService(),
          throwExceptionIfCannotObtainLock,
          enabled);
    }

    @Override
    protected Builder returnInstance() {
      return this;
    }

  }

  public static class MongockApplicationRunner extends ChangockSpringBuilderBase.ChangockSpringApplicationRunner {

    protected MongockApplicationRunner(SpringMigrationExecutor executor, ProfiledChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled) {
      super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled);
    }
  }

  public static class MongockInitializingBeanRunner extends ChangockSpringBuilderBase.ChangockSpringInitializingBeanRunner {

    protected MongockInitializingBeanRunner(SpringMigrationExecutor executor, ProfiledChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled) {
      super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled);
    }
  }

}
