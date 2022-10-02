package io.mongock.runner.core.executor.operation.migrate;

import io.mongock.api.config.executor.ChangeExecutorConfiguration;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.runner.core.executor.changelog.ChangeLogRuntime;
import io.mongock.runner.core.executor.changelog.ChangeLogServiceBase;
import java.lang.reflect.AnnotatedElement;
import java.util.function.Function;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class MigrateAllExecutor extends MigrateExecutorBase {


  public MigrateAllExecutor(String executionId,
                           ChangeLogServiceBase changeLogService,
                           ConnectionDriver driver,
                           ChangeLogRuntime changeLogRuntime,
                           Function<AnnotatedElement, Boolean> annotationFilter,
                           ChangeExecutorConfiguration config) {
    super(executionId, changeLogService, driver, changeLogRuntime, annotationFilter, config);
  }
}
