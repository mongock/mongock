package com.github.cloudyrock.mongock.decorator;

import java.lang.reflect.Method;

public class DecoratorMethodFailure {


  private final Class type;

  private final Method method;

  private final boolean errorEnsuringLock;

  private final boolean errorReturningDecorator;

  private final boolean otherError;

  private final String errorDetail;

  public static DecoratorMethodFailure otherError(Class type, Method method)  {
    return otherError(type, method, "");
  }

  public static DecoratorMethodFailure otherError(Class type, Method method, String details)  {
    return new DecoratorMethodFailure(type, method, false, false, true, details);
  }

  public DecoratorMethodFailure(Class type, Method method, boolean errorReturningDecorator, boolean errorEnsuringLock) {
    this(type, method, errorReturningDecorator, errorEnsuringLock, false, "");
  }

  private DecoratorMethodFailure(Class type, Method method, boolean errorReturningDecorator, boolean errorEnsuringLock, boolean otherError, String errorDetail) {
    this.type = type;
    this.method = method;
    this.errorReturningDecorator = errorReturningDecorator;
    this.errorEnsuringLock = errorEnsuringLock;
    this.otherError = otherError;
    this.errorDetail = errorDetail;
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

    StringBuilder sb = new StringBuilder(type.getSimpleName()).append(".").append(method.getName()).append(" -> (");
    if(errorReturningDecorator) {
      sb.append(" no_decorator");
    }
    if(errorEnsuringLock) {
      sb.append(" no_lock");
    }
    if(otherError) {
      sb.append(" other_error: ") .append(errorDetail);
    }
    return sb.append(" )").toString();
  }
}
