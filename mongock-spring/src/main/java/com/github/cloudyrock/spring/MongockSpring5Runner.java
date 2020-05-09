package com.github.cloudyrock.spring;

import com.github.cloudyrock.mongock.MongockAnnotationProcessor;
import com.github.cloudyrock.mongock.MongockConnectionDriver;
import io.changock.runner.core.builder.DriverBuilderConfigurable;
import io.changock.runner.spring.v5.ChangockSpringBuilderBase;
import io.changock.runner.spring.v5.ProfiledChangeLogService;
import io.changock.runner.spring.v5.SpringMigrationExecutor;

public class MongockSpring5Runner {
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

    public MongockInitializingBean buildInitializingBeanRunner() {
      return new MongockInitializingBean(
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

  public static class MongockInitializingBean extends ChangockSpringBuilderBase.ChangockSpringInitializingBeanRunner {

    protected MongockInitializingBean(SpringMigrationExecutor executor, ProfiledChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled) {
      super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled);
    }
  }

}
