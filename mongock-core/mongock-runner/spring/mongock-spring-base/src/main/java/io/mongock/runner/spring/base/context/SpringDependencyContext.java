package io.mongock.runner.spring.base.context;

import io.mongock.runner.core.executor.dependency.DependencyContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

public class SpringDependencyContext implements DependencyContext {

  private final ApplicationContext springContext;

  public SpringDependencyContext(ApplicationContext springContext) {
    this.springContext = springContext;
  }

  public ApplicationContext getSpringContext() {
    return springContext;
  }

  @Override
  public <T> Optional<T> getBean(Class<T> type) {
    try {
      return Optional.ofNullable(springContext.getBean(type));
    } catch (BeansException ex) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<Object> getBean(String name) {
    try {
      return Optional.ofNullable(springContext.getBean(name));
    } catch (BeansException ex) {
      return Optional.empty();
    }
  }
}
