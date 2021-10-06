package io.mongock.driver.api.common;

public class ForbiddenParameterException extends DependencyInjectionException {

  private final Class<?> replacementClass;

  public ForbiddenParameterException(Class forbiddenClass, Class<?> replacementClass) {
    super(forbiddenClass);
    this.replacementClass = replacementClass;
  }

  public Class<?> getReplacementClass() {
    return replacementClass;
  }

  @Override
  public String getMessage() {
    return String.format("Forbidden parameter[%s]. Must be replaced with [%s]", getWrongParameter().getSimpleName(), replacementClass.getSimpleName());
  }
}
