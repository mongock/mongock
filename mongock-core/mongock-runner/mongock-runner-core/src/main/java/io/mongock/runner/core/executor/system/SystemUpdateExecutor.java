package io.mongock.runner.core.executor.system;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.api.config.TransactionStrategy;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.common.SystemChange;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.api.driver.DriverLegaciable;
import io.mongock.driver.api.driver.DriverSystemUpdatable;
import io.mongock.driver.api.lock.LockManager;
import io.mongock.runner.core.executor.changelog.ChangeLogRuntime;
import io.mongock.runner.core.executor.changelog.ChangeLogServiceBase;
import io.mongock.runner.core.executor.ChangeExecutorBase;
import io.mongock.runner.core.internal.ChangeLogItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.concurrent.NotThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NotThreadSafe
public class SystemUpdateExecutor<CONFIG extends MongockConfiguration> extends ChangeExecutorBase<CONFIG> {
  
  private static final Logger logger = LoggerFactory.getLogger(SystemUpdateExecutor.class);
  
  private static final String SYSTEM_CHANGES_DEFAULT_AUTHOR = "mongock";
  
  private final List<String> basePackages;
  
    
  public SystemUpdateExecutor(
          String executionId,
          ConnectionDriver driver,
          ChangeLogServiceBase changeLogService,
          ChangeLogRuntime changeLogRuntime,
          CONFIG config,
          List<String> basePackages) {
    super(executionId,
          changeLogService,
          driver,
          changeLogRuntime,
          null,
          null,
          config.getServiceIdentifier(),
          false,
          config.getTransactional(),
          config.getTransactionEnabled(),
          TransactionStrategy.CHANGE_UNIT,
          config);
    this.basePackages = basePackages;
  }

  @Override
  public Boolean executeMigration() {
    initializationAndValidation();
    // Fetch existing system changes
    Collection<ChangeLogItem> changeLogs = changeLogService.fetchChangeLogs();
    try {
      if (changeLogs == null || changeLogs.isEmpty()) {
        logger.info("Mongock skipping the system update execution. There is no system change set item.");
        return false;
      }
      // load executed changeEntries to check if any of the changeSets need to be executed
      loadExecutedChangeEntries();
      if (!this.isThereAnyChangeSetItemToBeExecuted(changeLogs)) {
        logger.info("Mongock skipping the system update execution. All system change set items are already executed.");
        logIgnoredChangeLogs(changeLogs);
        return false;
      }
      try (LockManager lockManager = driver.getLockManager()) {
        lockManager.acquireLockDefault();
        // when lock is acquired, it's needed to reload executed changeEntries to get last changes
        loadExecutedChangeEntries();
        String executionHostname = generateExecutionHostname(executionId);
        logger.info("Mongock starting the system update execution id[{}]...", executionId);
        processMigration(changeLogs, executionId, executionHostname);
        return true;
      }
    }
    catch (Exception ex) {
      logger.error("Mongock has failed executing system updates");
      throw ex;
    } finally {
      this.executionInProgress = false;
      logger.info("Mongock has finished the system update execution");
    }
  }
  
  @Override
  protected void prepareChangeLogService() {
    List<String> changeLogsScanPackage = new ArrayList<>();
    if (basePackages != null) {
      changeLogsScanPackage.addAll(basePackages);
    }
    DriverSystemUpdatable driverSystemUpdatable = this.getDriverSystemUpdatable(driver);
    if (driverSystemUpdatable != null &&
        driverSystemUpdatable.getSystemUpdateChangesPackage() != null &&
        !driverSystemUpdatable.getSystemUpdateChangesPackage().trim().isEmpty()) {
      changeLogsScanPackage.add(driverSystemUpdatable.getSystemUpdateChangesPackage());
    }
    if (config.getLegacyMigration() != null) {
      DriverLegaciable legaciableDriver = this.getDriverLegaciable(driver);
      if (legaciableDriver != null) {
        changeLogsScanPackage.add(legaciableDriver.getLegacyMigrationChangeLogClass(config.getLegacyMigration().isRunAlways()).getPackage().getName());
      }
    }
    changeLogService.reset();
    changeLogService.setChangeLogsBasePackageList(changeLogsScanPackage);
    changeLogService.setDefaultAuthor(SYSTEM_CHANGES_DEFAULT_AUTHOR);
  }
    
  private DriverSystemUpdatable getDriverSystemUpdatable(ConnectionDriver driver) {
    return driver != null && DriverSystemUpdatable.class.isAssignableFrom(driver.getClass()) ? (DriverSystemUpdatable) driver : null;
  }
  
  private DriverLegaciable getDriverLegaciable(ConnectionDriver driver) {
    return driver != null && DriverLegaciable.class.isAssignableFrom(driver.getClass()) ? (DriverLegaciable) driver : null;
  }
  
  @Override
  protected void validateChangeLog(ChangeLogItem changeLog) {
    if (!changeLog.isSystem()) {
      throw new MongockException("Invalid system ChangeUnit[%s]. It needs to be annotated with @%s", changeLog.getId(), SystemChange.class.getSimpleName());
    }
  }
}
