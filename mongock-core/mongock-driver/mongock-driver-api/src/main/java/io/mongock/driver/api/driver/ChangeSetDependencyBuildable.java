package io.mongock.driver.api.driver;

import java.util.function.Function;

public class DecoratorChangeSetDependency<T, IMPL> extends ChangeSetDependency{

  private final Function<IMPL, T> decoratorFunction;

  public DecoratorChangeSetDependency(Class<T> type, Class<IMPL> implType, Function<IMPL, T> decoratorFunction) {
    super(DEFAULT_NAME, type, null, false);
    this.decoratorFunction = decoratorFunction;
  }

  public void setImpl(IMPL impl) {
    this.instance = impl;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object getInstance() {
    return decoratorFunction.apply((IMPL)instance);
  }

}
