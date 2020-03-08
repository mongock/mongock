package com.github.cloudyrock.mongock;

import io.changock.runner.spring.v5.ChangockSpringApplicationRunner;
import io.changock.runner.spring.v5.ChangockSpringInitializingBeanRunner;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.io.Closeable;

public class SpringMongock implements MongockBase, InitializingBean {


  private final ChangockSpringInitializingBeanRunner runner;

  SpringMongock(ChangockSpringInitializingBeanRunner runner) {
    this.runner = runner;
  }

  /**
   * For Spring users: executing mongock after bean is created in the Spring context
   */
  @Override
  public void afterPropertiesSet() {
    execute();
  }

  public void execute() {
    this.runner.execute();
  }
}
