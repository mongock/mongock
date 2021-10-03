package io.mongock.runner.springboot.base.builder;

import io.mongock.runner.springboot.base.MongockApplicationRunner;
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner;

public interface SpringApplicationBean {
  //TODO javadoc
  MongockApplicationRunner buildApplicationRunner();

  //TODO javadoc
  MongockInitializingBeanRunner buildInitializingBeanRunner();
}
