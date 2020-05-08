package com.github.cloudyrock.mongock;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.runner.spring.v5.ChangockSpring5Runner;
import org.springframework.context.ApplicationContext;

public class SpringMongockBuilder extends MongockBuilderBase<SpringMongockBuilder, MongockInitializingBeanRunner> {

  private ApplicationContext springContext;

  /**
   * <p>Builder constructor takes new API com.mongodb.client.MongoClient, database name and changelog scan package as parameters.</p>
   * <p>For more details about MongoClient please see om.mongodb.client.MongoClient docs</p>
   *
   * @param changeLogsScanPackage package path where the changelogs are located
   */
  public SpringMongockBuilder(String changeLogsScanPackage) {
    super(changeLogsScanPackage);
  }

  /**
   * Set the Springboot application springContext from which the dependencies will be retrieved
   *
   * @param context Springboot application springContext
   * @return Mongock builder for fluent interface
   * @see org.springframework.context.ApplicationContext
   */
  public SpringMongockBuilder setApplicationContext(ApplicationContext context) {
    this.springContext = context;
    return this;
  }


  /**
   * Builds Mongock runner instance as Spring InitializingBeanRunner
   * @return Mongock runner instance as Spring InitializingBeanRunner
   * @see org.springframework.beans.factory.InitializingBean
   */
  public MongockInitializingBeanRunner buildInitializingBeanRunner() {
    return new MongockInitializingBeanRunner(getBuilder(driver).buildInitializingBeanRunner());
  }

  /**
   * Builds Mongock runner instance as Spring ApplicationRunner
   * @return Mongock runner instance as Spring ApplicationRunner
   * @see org.springframework.boot.ApplicationRunner
   */
  public MongockApplicationRunner buildApplicationRunner() {
    return new MongockApplicationRunner(getBuilder(driver).buildApplicationRunner());
  }

  private ChangockSpring5Runner.ChangockSpring5RunnerBuilder getBuilder(ConnectionDriver driver) {
    return ChangockSpring5Runner.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(changeLogsScanPackage)
        .setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock)
        .setLockConfig(lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries)
        .setEnabled(enabled)
        .setStartSystemVersion(startSystemVersion)
        .setEndSystemVersion(endSystemVersion)
        .withMetadata(metadata)
        .setSpringContext(springContext)
        .overrideAnnoatationProcessor(new MongockAnnotationProcessor());
  }



  @Override
  protected SpringMongockBuilder getInstance() {
    return this;
  }

}
