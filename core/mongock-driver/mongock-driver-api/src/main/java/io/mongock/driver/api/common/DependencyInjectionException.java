package io.mongock.driver.api.common;

import io.mongock.api.exception.MongockException;

public class DependencyInjectionException extends MongockException {

  private final Class<?> wrongParameter;
  private final String name;

  public DependencyInjectionException(Class wrongParameter) {
    this(wrongParameter, null);
  }

  public DependencyInjectionException(Class wrongParameter, String name) {
    super();
    this.wrongParameter = wrongParameter;
    this.name = name;
  }

  public Class<?> getWrongParameter() {
    return wrongParameter;
  }

  public String getName() {
    return name;
  }

  @Override
  public String getMessage() {
    StringBuilder sb = new StringBuilder("Wrong parameter[")
        .append(getWrongParameter().getSimpleName())
        .append("]");
    if (name != null) {
      sb.append(" with name: ")
          .append(name);
    }
    sb.append(". Dependency not found.");
    return sb.toString();
  }
}
