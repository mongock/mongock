package com.github.cloudyrock.spring.v5;

import com.github.cloudyrock.mongock.MongockAnnotationProcessor;
import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.migration.api.config.ChangockSpringConfiguration;
import io.changock.runner.core.builder.DriverBuilderConfigurable;
import io.changock.runner.spring.util.SpringEventPublisher;
import io.changock.runner.spring.v5.ChangockSpringBuilderBase;
import io.changock.runner.spring.v5.SpringApplicationRunner;
import io.changock.runner.spring.v5.SpringInitializingBean;
import io.changock.runner.spring.v5.core.ProfiledChangeLogService;
import io.changock.runner.spring.v5.core.SpringMigrationExecutor;

public class MongockSpring5 {
  public static DriverBuilderConfigurable<Builder, ConnectionDriver, ChangockSpringConfiguration> builder() {
    return new Builder();
  }

  public static class Builder extends ChangockSpringBuilderBase<Builder, ConnectionDriver, ChangockSpringConfiguration> {

    private Builder() {
      overrideAnnoatationProcessor(new MongockAnnotationProcessor());
    }

    public MongockApplicationRunner buildApplicationRunner() {
      return new MongockApplicationRunner(
          buildExecutorWithEnvironmentDependency(),
          buildProfiledChangeLogService(),
          throwExceptionIfCannotObtainLock,
          enabled,
          buildSpringEventPublisher());
    }

    public MongockInitializingBeanRunner buildInitializingBeanRunner() {
      return new MongockInitializingBeanRunner(
          buildExecutorWithEnvironmentDependency(),
          buildProfiledChangeLogService(),
          throwExceptionIfCannotObtainLock,
          enabled,
          buildSpringEventPublisher());
    }

    @Override
    protected Builder returnInstance() {
      return this;
    }

  }

  /**
   * This class will be removed in following versions.
   * Please use the equivalent class SpringApplicationRunner.
   * @see SpringApplicationRunner
   */
  @Deprecated
  public static class MongockApplicationRunner extends SpringApplicationRunner {

    protected MongockApplicationRunner(SpringMigrationExecutor executor, ProfiledChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled, SpringEventPublisher eventPublisher) {
      super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
    }
  }

  /**
   * This class will be removed in following versions.
   * Please use the equivalent class SpringInitializingBean.
   * @see SpringInitializingBean
   */
  @Deprecated
  public static class MongockInitializingBeanRunner extends SpringInitializingBean {

    protected MongockInitializingBeanRunner(SpringMigrationExecutor executor, ProfiledChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled, SpringEventPublisher eventPublisher) {
      super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
    }
  }

}
