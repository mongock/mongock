package io.mongock.runner.core.executor;

import io.mongock.api.config.executor.ExecutorConfiguration;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.runner.core.executor.changelog.ChangeLogRuntime;
import io.mongock.runner.core.executor.changelog.ChangeLogServiceBase;
import io.mongock.runner.core.executor.operation.Operation;

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
  
  ///////////////////////////////////////////////////////////////////////////////////
  //  Build methods
  ///////////////////////////////////////////////////////////////////////////////////
  @Override
  public Executor buildExecutor() {
    validateCommonInfo();
    return getExecutorByOperation(operation);
  }
  
  protected abstract Executor getExecutorByOperation(Operation operation);
  
  ///////////////////////////////////////////////////////////////////////////////////
  //  Validation and aux methods
  ///////////////////////////////////////////////////////////////////////////////////
  
  private void validateCommonInfo() {
    
    if (operation == null) {
      throw new MongockException("operation cannot be null");
    }
    
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
  
  protected void validateScanPackage() {
    if (config.getMigrationScanPackage() == null || config.getMigrationScanPackage().isEmpty()) {
      throw new MongockException("Scan package for changeLogs is not set: use appropriate setter");
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


