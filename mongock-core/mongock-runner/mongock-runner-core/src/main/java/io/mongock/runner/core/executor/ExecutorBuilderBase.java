package io.mongock.runner.core.executor;

import io.mongock.api.config.executor.ExecutorConfiguration;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.runner.core.executor.changelog.ChangeLogRuntime;
import io.mongock.runner.core.executor.changelog.ChangeLogServiceBase;
import io.mongock.runner.core.executor.operation.Operation;
import java.lang.reflect.AnnotatedElement;
import java.util.function.Function;

public abstract class ExecutorBuilderBase<CONFIG extends ExecutorConfiguration>
                                        implements ExecutorBuilder<CONFIG> {
  
  // Mandatory
  protected Operation operation;
  protected String executionId;
  protected ConnectionDriver driver;
  protected CONFIG config;
  
  // Optional
  protected ChangeLogServiceBase changeLogService;
  protected ChangeLogRuntime changeLogRuntime;
  protected Function<AnnotatedElement, Boolean> annotationFilter;

  ///////////////////////////////////////////////////////////////////////////////////
  //  SETTERS
  ///////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public ExecutorBuilder<CONFIG> setOperation(Operation operation) {
    this.operation = operation;
    return this;
  }
  
  @Override
  public ExecutorBuilder<CONFIG> setExecutionId(String executionId) {
    this.executionId = executionId;
    return this;
  }
  
  @Override
  public ExecutorBuilder<CONFIG> setDriver(ConnectionDriver driver) {
    this.driver = driver;
    return this;
  }
  
  @Override
  public ExecutorBuilder<CONFIG> setConfig(CONFIG config) {
    this.config = config;
    return this;
  }
  
  @Override
  public ExecutorBuilder<CONFIG> setChangeLogService(ChangeLogServiceBase changeLogService) {
    this.changeLogService = changeLogService;
    return this;
  }
  
  @Override
  public ExecutorBuilder<CONFIG> setChangeLogRuntime(ChangeLogRuntime changeLogRuntime) {
    this.changeLogRuntime = changeLogRuntime;
    return this;
  }
  
  @Override
  public ExecutorBuilder<CONFIG> setAnnotationFilter(Function<AnnotatedElement, Boolean> annotationFilter) {
    this.annotationFilter = annotationFilter;
    return this;
  }
  
  ///////////////////////////////////////////////////////////////////////////////////
  //  Build methods
  ///////////////////////////////////////////////////////////////////////////////////
  @Override
  public Executor buildSystemExecutor() {
    validateCommonInfo();
    return getSystemExecutor();
  }
  
  protected abstract Executor getSystemExecutor();
  
  @Override
  public Executor buildOperationExecutor() {
    validateOperation();
    validateCommonInfo();
    return getExecutorByOperation(operation);
  }
  
  protected abstract Executor getExecutorByOperation(Operation operation);
  
  ///////////////////////////////////////////////////////////////////////////////////
  //  Validation and aux methods
  ///////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public ExecutorBuilder<CONFIG> reset() {
    this.operation = null;
    this.executionId = null;
    this.driver = null;
    this.config = null;
    this.changeLogService = null;
    this.changeLogRuntime = null;
    this.annotationFilter = null;
    return this;
  }
  
  private void validateCommonInfo() {
    
    if (executionId == null || executionId.trim().isEmpty()) {
      throw new MongockException("executionId cannot be null or empty");
    }
    
    if (driver == null) {
      throw new MongockException("driver cannot be null");
    }
    
    if (config == null) {
      throw new MongockException("config cannot be null");
    }
  }
  
  protected void validateOperation() {
    if (operation == null) {
      throw new MongockException("operation cannot be null");
    }
  }
  
  protected void validateScanPackage() {
    if (config.getMigrationScanPackage() == null || config.getMigrationScanPackage().isEmpty()) {
      throw new MongockException("Please, provide a Scan package for changeLogs or a file to reade the ChangeUnit's classes from");
    }
  }
  
  protected void validateChangeLogService() {
    if (changeLogService == null) {
      throw new MongockException("changeLogService cannot be null");
    }
  }
  
  protected void validateChangeLogRuntime() {
    if (changeLogRuntime == null) {
      throw new MongockException("changeLogRuntime cannot be null");
    }
  }
}


