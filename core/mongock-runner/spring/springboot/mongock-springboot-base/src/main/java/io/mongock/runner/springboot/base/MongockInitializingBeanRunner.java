package io.mongock.runner.springboot.base;

import io.mongock.runner.core.executor.MongockRunner;
import org.springframework.beans.factory.InitializingBean;

public class MongockInitializingBeanRunner implements InitializingBean {

  private final MongockRunner runner;


  public MongockInitializingBeanRunner(MongockRunner runner) {
    this.runner = runner;
  }

  @Override
  public void afterPropertiesSet() {
    runner.execute();
  }
}
