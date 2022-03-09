package io.mongock.runner.core.executor;

import io.mongock.api.config.executor.ExecutorConfiguration;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.runner.core.executor.changelog.ChangeLogRuntime;
import io.mongock.runner.core.executor.changelog.ChangeLogServiceBase;
import io.mongock.runner.core.executor.operation.Operation;
import io.mongock.runner.core.internal.ChangeLogItem;
import io.mongock.runner.core.internal.ChangeSetItem;

public abstract class ExecutorBuilderBase<CHANGELOG extends ChangeLogItem<CHANGESET>,
                                          CHANGESET extends ChangeSetItem,
                                          CONFIG extends ExecutorConfiguration>
                                        implements ExecutorBuilder<CHANGELOG, CHANGESET, CONFIG> {
  
  // Mandatory
  protected Operation operation;
  protected String executionId;
  protected ConnectionDriver driver;
  protected CONFIG config;
  
  // Optional
  protected ChangeLogServiceBase<CHANGELOG, CHANGESET> changeLogService;
  protected ChangeLogRuntime changeLogRuntime;

  ///////////////////////////////////////////////////////////////////////////////////
  //  SETTERS
  ///////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public ExecutorBuilder<CHANGELOG, CHANGESET, CONFIG> setOperation(Operation operation) {
    this.operation = operation;
    return this;
  }
  
  @Override
  public ExecutorBuilder<CHANGELOG, CHANGESET, CONFIG> setExecutionId(String executionId) {
    this.executionId = executionId;
    return this;
  }
  
  @Override
  public ExecutorBuilder<CHANGELOG, CHANGESET, CONFIG> setDriver(ConnectionDriver driver) {
    this.driver = driver;
    return this;
  }
  
  @Override
  public ExecutorBuilder<CHANGELOG, CHANGESET, CONFIG> setConfig(CONFIG config) {
    this.config = config;
    return this;
  }
  
  @Override
  public ExecutorBuilder<CHANGELOG, CHANGESET, CONFIG> setChangeLogService(ChangeLogServiceBase<CHANGELOG, CHANGESET> changeLogService) {
    this.changeLogService = changeLogService;
    return this;
  }
  
  @Override
  public ExecutorBuilder<CHANGELOG, CHANGESET, CONFIG> setChangeLogRuntime(ChangeLogRuntime changeLogRuntime) {
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


