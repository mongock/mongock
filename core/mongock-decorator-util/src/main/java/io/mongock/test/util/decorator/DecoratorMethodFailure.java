package io.mongock.test.util.decorator;

import java.lang.reflect.Method;
import java.util.List;

public class DecoratorMethodFailure {


  private static final int MAX_SIZE_ERRORS_MESSAGE = 100;
  private final Class<?> type;

  private final Method method;

  private final boolean errorEnsuringLock;

  private final boolean errorReturningDecorator;

  private final boolean otherError;

  private final String errorDetail;

  public static DecoratorMethodFailure otherError(Class type, Method method) {
    return otherError(type, method, "");
  }

  public static DecoratorMethodFailure otherError(Class type, Method method, String details) {
    return new DecoratorMethodFailure(type, method, false, false, details);
  }

  public DecoratorMethodFailure(Class type, Method method, boolean errorReturningDecorator, boolean errorEnsuringLock) {
    this(type, method, errorReturningDecorator, errorEnsuringLock, "");
  }

  public DecoratorMethodFailure(Class type, Method method, boolean errorReturningDecorator, boolean errorEnsuringLock, String errorDetail) {
    this.type = type;
    this.method = method;
    this.errorReturningDecorator = errorReturningDecorator;
    this.errorEnsuringLock = errorEnsuringLock;
    this.otherError = errorDetail != null && !errorDetail.isEmpty();
    this.errorDetail = errorDetail != null ? errorDetail : "";
  }

  public Method getMethod() {
    return method;
  }

  public boolean isErrorEnsuringLock() {
    return errorEnsuringLock;
  }

  public boolean isErrorReturningDecorator() {
    return errorReturningDecorator;
  }

  public boolean isOtherError() {
    return otherError;
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder()
        .append(method.getReturnType().getSimpleName())
        .append(" ")
        .append(type.getSimpleName())
        .append(".")
        .append(method.getName())
        .append("(");

    boolean commaParams = false;
    for(int i = 0 ; i< method.getParameterTypes().length ; i++) {
      sb.append(commaParams ? ", " : "").append(method.getParameterTypes()[i].getSimpleName());
      commaParams = true;
    }
    sb.append(")").append(" -> ");
    boolean coma = false;
    if (errorReturningDecorator) {
      sb.append(" no returned decorator");
      coma = true;
    }
    if (errorEnsuringLock) {
      sb.append(coma ? ", " : "").append(" no ensured lock");
      coma = true;
    }
    if (otherError) {
      sb.append(coma ? ", " : "").append(errorDetail);
      coma = true;
    }
    return sb.toString();
  }

  public static String printErrorMessage(List<DecoratorMethodFailure> errors) {
    StringBuilder sb = new StringBuilder();
    if (errors != null && !errors.isEmpty()) {
      sb.append("Decorators errors(").append(errors.size()).append(")").append("\n");
      errors.stream()
          .limit(MAX_SIZE_ERRORS_MESSAGE)
          .map(DecoratorMethodFailure::toString)
          .forEach(msg -> sb.append(msg).append("\n"));
      if (errors.size() > MAX_SIZE_ERRORS_MESSAGE) {
        sb.append("...and ").append(errors.size() - MAX_SIZE_ERRORS_MESSAGE).append(" more\n");
      }
    }
    return sb.toString();
  }
}
