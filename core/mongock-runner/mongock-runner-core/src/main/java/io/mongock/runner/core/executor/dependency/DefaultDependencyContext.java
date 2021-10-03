package io.mongock.runner.core.executor.dependency;

import java.util.Optional;
import java.util.function.Function;

public class DefaultDependencyContext implements DependencyContext {


  private final Function<Class, Optional<Object>> byType;
  private final Function<String, Optional<Object>> byName;

  public DefaultDependencyContext(Function<Class, Optional<Object>> byType, Function<String, Optional<Object>> byName) {
    this.byType = byType;
    this.byName = byName;
  }

  @Override
  public <T> Optional<T> getBean(Class<T> type) {
    return (Optional<T>) byType.apply(type);
  }

  @Override
  public Optional<Object> getBean(String name) {
    return byName.apply(name);
  }
}
