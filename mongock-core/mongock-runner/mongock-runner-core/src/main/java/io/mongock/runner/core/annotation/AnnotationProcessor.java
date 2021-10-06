package io.mongock.runner.core.annotation;

import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.api.exception.MongockException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

public class AnnotationProcessor {
  public Method getExecuteMethod(Class<?> changeUnitClass) {
    Class<Execution> annotation = Execution.class;
    return findMethodByAnnotation(changeUnitClass, annotation)
        .orElseThrow(() -> new MongockException("ChangeUnit[%s] without %s method", changeUnitClass.getName(), annotation.getSimpleName()));
  }
  public Method getRollbackMethod(Class<?> changeUnitClass) {
    Class<RollbackExecution> annotation = RollbackExecution.class;
    return findMethodByAnnotation(changeUnitClass, annotation)
        .orElseThrow(() -> new MongockException("ChangeUnit[%s] without %s method", changeUnitClass.getName(), annotation.getSimpleName()));

  }

  public Optional<Method> getBeforeMethod(Class<?> changeUnitClass) {
    return findMethodByAnnotation(changeUnitClass, BeforeExecution.class);
  }

  public Optional<Method> getRollbackBeforeMethod(Class<?> changeUnitClass) {
    return findMethodByAnnotation(changeUnitClass, RollbackBeforeExecution.class);
  }

  private Optional<Method> findMethodByAnnotation(Class<?> changeUnitClass, Class<? extends Annotation> annotation) {
    return getMethodStreamByAnnotation(changeUnitClass, annotation)
        .findAny();
  }

  private Stream<Method> getMethodStreamByAnnotation(Class<?> changeUnitClass, Class<? extends Annotation> annotation) {
    return Stream.of(changeUnitClass.getMethods())
        .filter(method -> method.isAnnotationPresent(annotation));
  }

  public void validateChangeUnit(Class<?> changeUnitClass) {

    boolean error = false;
    StringBuilder errorMessage = new StringBuilder();

    // One(and only one) execution method
    long executionCount = getMethodStreamByAnnotation(changeUnitClass, Execution.class).count();
    if(executionCount != 1) {
      error = true;
      errorMessage.append(String.format("ChangeUnit[%s] must have only one %s method", changeUnitClass.getName(), Execution.class.getSimpleName()))
          .append("\n");
    }

    // One(and only one) rollback method
    long rollbackCount = getMethodStreamByAnnotation(changeUnitClass, RollbackExecution.class).count();
    if(rollbackCount != 1) {
      error = true;
      errorMessage.append(String.format("ChangeUnit[%s] must have only one %s method", changeUnitClass.getName(), RollbackExecution.class.getSimpleName()))
          .append("\n");
    }

    //At most one Before method
    long beforeCount = getMethodStreamByAnnotation(changeUnitClass, BeforeExecution.class).count();
    if(beforeCount > 1) {
      error = true;
      errorMessage.append(String.format("ChangeUnit[%s] can have at most one %s method", changeUnitClass.getName(), BeforeExecution.class.getSimpleName()))
          .append("\n");
    } else if(beforeCount == 1) {
      //If before method provided, one(and only one) rollbackBeforeMethod
      long rollbackBeforeCount = getMethodStreamByAnnotation(changeUnitClass, RollbackBeforeExecution.class).count();
      if(rollbackBeforeCount != 1) {
        error = true;
        errorMessage.append(String.format("When ChangeUnit[%s] provides %s method, it must provide one(and only one) %s method", changeUnitClass.getName(), BeforeExecution.class.getSimpleName(), RollbackBeforeExecution.class.getSimpleName()))
            .append("\n");
      }
    }
    if(error) {
      throw new MongockException(errorMessage.toString());
    }

  }
}
