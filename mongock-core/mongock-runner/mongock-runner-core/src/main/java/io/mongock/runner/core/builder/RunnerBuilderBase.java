package io.mongock.runner.core.builder;

import io.mongock.runner.core.internal.ChangeLogItem;
import io.mongock.runner.core.internal.ChangeSetItem;
import io.mongock.api.config.LegacyMigration;
import io.mongock.api.config.MongockConfiguration;
import io.mongock.api.config.MongockConstants;
import io.mongock.api.config.TransactionStrategy;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.driver.ChangeSetDependency;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.api.driver.DriverLegaciable;
import io.mongock.runner.core.event.EventPublisher;
import io.mongock.runner.core.executor.ChangeLogRuntimeImpl;
import io.mongock.runner.core.executor.Executor;
import io.mongock.runner.core.executor.ExecutorFactory;
import io.mongock.runner.core.executor.MongockRunner;
import io.mongock.runner.core.executor.MongockRunnerImpl;
import io.mongock.runner.core.executor.changelog.ChangeLogServiceBase;
import io.mongock.runner.core.executor.dependency.DependencyManager;
import io.mongock.runner.core.executor.operation.Operation;
import io.mongock.runner.core.executor.operation.change.MigrationOp;
import io.mongock.utils.MongockCommunityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.function.Function;


public abstract class RunnerBuilderBase<
    SELF extends RunnerBuilderBase<SELF, CHANGELOG, CHANGESET, CONFIG>,
    CHANGELOG extends ChangeLogItem<CHANGESET>,
    CHANGESET extends ChangeSetItem,
    CONFIG extends MongockConfiguration> {

  private static final Logger logger = LoggerFactory.getLogger(RunnerBuilderBase.class);
  protected final CONFIG config;//todo make it independent from external configuration
  protected final ExecutorFactory<CHANGELOG, ? extends ChangeSetItem, CONFIG> executorFactory;
  protected final ChangeLogServiceBase<CHANGELOG, CHANGESET> changeLogService;
  protected final DependencyManager dependencyManager;
  private final BuilderType type;
  protected EventPublisher eventPublisher = new EventPublisher();
  protected ConnectionDriver driver;
  protected Function<Class<?>, Object> changeLogInstantiatorFunctionForAnnotations;
  protected Function<Parameter, String> parameterNameFunction = parameter -> parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;

  //todo move to config
  private String executionId = String.format("%s-%d", LocalDateTime.now(), new Random().nextInt(999));

  protected RunnerBuilderBase(BuilderType type,
                              ExecutorFactory<CHANGELOG, ? extends ChangeSetItem, CONFIG> executorFactory,
                              ChangeLogServiceBase<CHANGELOG, CHANGESET> changeLogService,
                              DependencyManager dependencyManager,
                              CONFIG config) {
    this.executorFactory = executorFactory;
    this.changeLogService = changeLogService;
    this.dependencyManager = dependencyManager;
    this.config = config;
    this.type = type;
  }

  public BuilderType getType() {
    return type;
  }


  //Default implementation. It should override in the professional libs
  public String getVersion() {
    return MongockCommunityProperties.VERSION;
  }

  ///////////////////////////////////////////////////////////////////////////////////
  //  SETTERS
  ///////////////////////////////////////////////////////////////////////////////////

  public SELF setExecutionId(String executionId) {
    this.executionId = executionId;
    return getInstance();
  }

  @Deprecated
  public SELF setChangeLogInstantiator(Function<Class<?>, Object> changeLogInstantiator) {
    this.changeLogInstantiatorFunctionForAnnotations = changeLogInstantiator;
    return getInstance();
  }

  public CONFIG getConfig() {
    return config;
  }

  public SELF setConfig(CONFIG newConfig) {
    config.updateFrom(newConfig);
    return getInstance();
  }

  public SELF setDriver(ConnectionDriver driver) {
    this.driver = driver;
    return getInstance();
  }


  public DependencyManager getDependencyManager() {
    return dependencyManager;
  }

  public SELF setTransactionStrategy(TransactionStrategy transactionStrategy) {
    config.setTransactionStrategy(transactionStrategy);
    return getInstance();
  }
  
  public SELF setEventPublisher(EventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
    return getInstance();
  }

  ///////////////////////////////////////////////////////////////////////////////////
  //  Build methods
  ///////////////////////////////////////////////////////////////////////////////////

  public MongockRunner buildRunner() {
    return buildRunner(new MigrationOp());
  }


  public MongockRunner buildRunner(Operation operation) {
    return buildRunner(operation, driver);
  }

  protected MongockRunner buildRunner(ConnectionDriver driver) {
    return buildRunner(new MigrationOp(), driver);
  }



  protected MongockRunner buildRunner(Operation operation, ConnectionDriver driver) {
    logger.info("Mongock runner {} version[{}]", getType(), getVersion());
    validateConfigurationAndInjections(driver);
    try {
      beforeBuildRunner(driver);
      return new MongockRunnerImpl(
          buildExecutor(operation, driver),
          config.isThrowExceptionIfCannotObtainLock(),
          config.isEnabled(),
          eventPublisher);
    } catch (MongockException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new MongockException(ex);
    }
  }

  protected void beforeBuildRunner(ConnectionDriver driver) {
    if (config.getLegacyMigration() != null) {
      DriverLegaciable legaciableDriver = this.getDriverLegaciable();
      if (legaciableDriver != null) {
        config.getChangeLogsScanPackage().add(legaciableDriver.getLegacyMigrationChangeLogClass(config.getLegacyMigration().isRunAlways()).getPackage().getName());
        dependencyManager.addStandardDependency(
            new ChangeSetDependency(MongockConstants.LEGACY_MIGRATION_NAME, LegacyMigration.class, config.getLegacyMigration())
        );
      }
    }
    driver.setLockRepositoryName(config.getLockRepositoryName());
    driver.setMigrationRepositoryName(config.getMigrationRepositoryName());
  }


  private SortedSet<CHANGELOG> getChangeLogs() {
    List<Class<?>> changeLogsScanClasses = new ArrayList<>();
    List<String> changeLogsScanPackage = new ArrayList<>();
    for (String itemPath : config.getMigrationScanPackage()) {
      try {
        changeLogsScanClasses.add(ClassLoader.getSystemClassLoader().loadClass(itemPath));
      } catch (ClassNotFoundException e) {
        changeLogsScanPackage.add(itemPath);
      }
    }
    changeLogService.setDefaultMigrationAuthor(config.getDefaultMigrationAuthor());
    changeLogService.setChangeLogsBasePackageList(changeLogsScanPackage);
    changeLogService.setChangeLogsBaseClassList(changeLogsScanClasses);
    changeLogService.setStartSystemVersion(config.getStartSystemVersion());
    changeLogService.setEndSystemVersion(config.getEndSystemVersion());
    changeLogService.setProfileFilter(getAnnotationFilter());
    return changeLogService.fetchChangeLogs();
  }


  protected void validateConfigurationAndInjections(ConnectionDriver driver) throws MongockException {
    if (driver == null) {
      throw new MongockException("Driver must be injected to Mongock builder");
    }
    if (config.getChangeLogsScanPackage() == null || config.getChangeLogsScanPackage().isEmpty()) {
      throw new MongockException("Scan package for changeLogs is not set: use appropriate setter");
    }
    if (!config.isThrowExceptionIfCannotObtainLock()) {
      logger.warn("throwExceptionIfCannotObtainLock is disabled, which means Mongock will continue even if it's not able to acquire the lock");
    }
    if (!"0".equals(config.getStartSystemVersion()) || !String.valueOf(Integer.MAX_VALUE).equals(config.getEndSystemVersion())) {
      logger.info("Running Mongock with startSystemVersion[{}] and endSystemVersion[{}]", config.getStartSystemVersion(), config.getEndSystemVersion());
    }
    if (config.getMetadata() == null) {
      logger.info("Running Mongock with NO metadata");
    } else {
      logger.info("Running Mongock with metadata");
    }

    if (config.getTransactionEnabled().isPresent()) {
      boolean transactionEnabled = config.getTransactionEnabled().get();
      if (transactionEnabled && !driver.isTransactionable()) {
        throw new MongockException("Property transaction-enabled=true, but transactionManager not provided");
      }

      if (!transactionEnabled && driver.isTransactionable()) {
        logger.warn("Property transaction-enabled=false, but driver is transactionable");
      }
    } else {
      logger.warn("Property transaction-enabled not provided. It will become true as default in next versions. Set explicit value to false in case transaction are not desired.");

      if (driver.isTransactionable()) {
        logger.warn("Property transaction-enabled not provided, but driver is transactionable. BY DEFAULT MONGOCK WILL RUN IN TRANSACTION MODE.");
      } else {
        logger.warn("Property transaction-enabled not provided and is unknown if driver is transactionable. BY DEFAULT MONGOCK WILL RUN IN NO-TRANSACTION MODE.");
      }
    }

  }


  protected Function<AnnotatedElement, Boolean> getAnnotationFilter() {
    return annotatedElement -> true;
  }

  protected DriverLegaciable getDriverLegaciable() {
    return this.driver;
  }

  protected  Executor buildExecutor(Operation operation, ConnectionDriver driver) {
    ChangeLogRuntimeImpl changeLogRuntime = new ChangeLogRuntimeImpl(
        changeLogInstantiatorFunctionForAnnotations,
        dependencyManager,
        parameterNameFunction,
        driver.getNonProxyableTypes());
    return executorFactory.getExecutor(
        operation,
        executionId,
        getChangeLogs(),
        driver,
        changeLogRuntime,
        config
    );
  }

  public abstract SELF getInstance();

}
