package io.mongock.runner.core.builder;

import com.github.cloudyrock.mongock.ChangeLog;
import io.mongock.api.ChangeLogItem;
import io.mongock.api.ChangeSetItem;
import io.mongock.api.config.MongockConfiguration;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.runner.core.builder.roles.ChangeLogScanner;
import io.mongock.runner.core.builder.roles.MigrationWriter;
import io.mongock.runner.core.builder.roles.Configurable;
import io.mongock.runner.core.builder.roles.DependencyInjectable;
import io.mongock.runner.core.builder.roles.DriverConnectable;
import io.mongock.runner.core.builder.roles.LegacyMigrator;
import io.mongock.runner.core.builder.roles.MongockRunnable;
import io.mongock.runner.core.builder.roles.SelfInstanstiator;
import io.mongock.runner.core.builder.roles.ServiceIdentificable;
import io.mongock.runner.core.builder.roles.SystemVersionable;
import io.mongock.runner.core.executor.ExecutorFactory;
import io.mongock.runner.core.executor.ExecutorFactoryDefault;
import io.mongock.runner.core.executor.changelog.ChangeLogService;
import io.mongock.runner.core.executor.dependency.DependencyManager;
import io.mongock.runner.core.util.LegacyMigrationDummyImpl;
import io.mongock.util.test.ReflectionUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static io.mongock.runner.core.builder.BuilderType.COMMUNITY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RunnerBuilderBaseTest {

  private static final String PACKAGE_PATH = "package";
  private static final String START_SYSTEM_VERSION = "start_system_version";
  private static final String END_SYSTEM_VERSION = "end_system_versions";
  private static final long LOCK_ACQ_MILLIS = 100 * 60 * 1000L;
  private static final long LOCK_TRY_FREQ_MILLIS = 1000L;
  private static final long LOCK_QUIT_TRY_MILLIS = 3 * 60 * 1000L;

  private static final Map<String, Object> METADATA = new HashMap<>();
  @Rule
  public ExpectedException exceptionExpected = ExpectedException.none();
  ConnectionDriver<ChangeEntry> driver = mock(ConnectionDriver.class);
  Map<String, Object> metadata = new HashMap<>();

  @Before
  @SuppressWarnings("all")
  public void before() {
    Class legacyMigrationChangeLogDummyClass = DummyRunnerBuilder.LegacyMigrationChangeLogDummy.class;
    when(driver.getLegacyMigrationChangeLogClass(Mockito.anyBoolean())).thenReturn(legacyMigrationChangeLogDummyClass);
  }

  @Test
  public void shouldAssignAllTheParameters() {
    new DummyRunnerBuilder(new ExecutorFactoryDefault())
        .setDriver(driver)
        .setEnabled(false)
        .setStartSystemVersion("start")
        .setEndSystemVersion("end")
        .dontFailIfCannotAcquireLock()
        .addChangeLogsScanPackage("package")
        .withMetadata(metadata)
        .validate();
  }

  @Test
  public void shouldCallAllTheMethods_whenSetConfig() {

    DummyRunnerBuilder builder = Mockito.spy(new DummyRunnerBuilder(new ExecutorFactoryDefault()).setDriver(driver));
    MongockConfiguration expectedConfig = getConfig(false, PACKAGE_PATH);
    builder.setConfig(expectedConfig);
    //todo check all the properties are set rightly
    MongockConfiguration actualConfig = (MongockConfiguration) ReflectionUtils.getPrivateField(builder, RunnerBuilderBase.class, "config");
    assertEquals(expectedConfig, actualConfig);
  }

  @Test
  public void shouldThrowExceptionTrueByDefault() {

    DummyRunnerBuilder builder = Mockito.spy(new DummyRunnerBuilder(new ExecutorFactoryDefault()).setDriver(driver));
    builder.setConfig(getConfig(null, PACKAGE_PATH));
    checkStandardBuilderCalls(builder);
    verify(builder, new Times(0)).dontFailIfCannotAcquireLock();
  }


  /**
   * SCAN PACKAGES
   */
  @Test
  public void shouldAddMultiplePackages_whenAddingList() {
    DummyRunnerBuilder builder = Mockito.spy(new DummyRunnerBuilder(new ExecutorFactoryDefault()).setDriver(driver));
    builder.addMigrationScanPackage("package1");
    builder.addMigrationScanPackage("package2");
    builder.addMigrationScanPackage("package3");
    verify(builder, new Times(1)).addMigrationScanPackages(Collections.singletonList("package1"));
    verify(builder, new Times(1)).addMigrationScanPackages(Collections.singletonList("package2"));
    verify(builder, new Times(1)).addMigrationScanPackages(Collections.singletonList("package3"));
  }


  @Test
  public void shouldAddMultiplePackages_whenMultiplePackagesFromConfig() {
    DummyRunnerBuilder builder = Mockito.spy(new DummyRunnerBuilder(new ExecutorFactoryDefault()).setDriver(driver));
    builder.setConfig(getConfig(null, "package1", "package2"));
    MongockConfiguration actualConfig = (MongockConfiguration) ReflectionUtils.getPrivateField(builder, RunnerBuilderBase.class, "config");
    assertTrue(actualConfig.getChangeLogsScanPackage().contains("package1"));
    assertTrue(actualConfig.getChangeLogsScanPackage().contains("package2"));
  }


  @Test
  public void shouldPropagateException_IfChangeLogServiceNotValidated() {

    RunnerBuilderBase builder = runnerBuilderBaseInstance();
    builder.setDriver(driver);

    exceptionExpected.expect(MongockException.class);
    exceptionExpected.expectMessage("Scan package for changeLogs is not set: use appropriate setter");
    builder.buildRunner();


  }


  @Test
  public void shouldPropagateException_IfFetchingLogsFails() {
    ChangeLogService changeLogService = mock(ChangeLogService.class);
    when(changeLogService.fetchChangeLogs()).thenThrow(new RuntimeException("ChangeLogService error"));

    RunnerBuilderBase builder = runnerBuilderBaseInstance(changeLogService);
    builder.setDriver(driver);
    MongockConfiguration config = new MongockConfiguration();
    config.setChangeLogsScanPackage(Collections.singletonList("package"));
    builder.setConfig(config);

    exceptionExpected.expect(MongockException.class);
    exceptionExpected.expectMessage("ChangeLogService error");
    builder.buildRunner();


  }


  private void checkStandardBuilderCalls(DummyRunnerBuilder builder) {

    MongockConfiguration actualConfig = (MongockConfiguration) ReflectionUtils.getPrivateField(builder, RunnerBuilderBase.class, "config");

    assertTrue(actualConfig.getChangeLogsScanPackage().contains(PACKAGE_PATH) && actualConfig.getChangeLogsScanPackage().size() == 1);
    assertFalse(actualConfig.isEnabled());
    assertEquals(START_SYSTEM_VERSION, actualConfig.getStartSystemVersion());
    assertEquals(END_SYSTEM_VERSION, actualConfig.getEndSystemVersion());
    assertEquals(METADATA, actualConfig.getMetadata());
  }

  private MongockConfiguration getConfig(Boolean throwEx, String... packages) {
    MongockConfiguration config = new DummyMongockConfiguration();
    config.setChangeLogsScanPackage(Arrays.asList(packages));
    config.setEnabled(false);
    config.setStartSystemVersion(START_SYSTEM_VERSION);
    config.setEndSystemVersion(END_SYSTEM_VERSION);
    config.setMetadata(METADATA);

    config.setLockAcquiredForMillis(LOCK_ACQ_MILLIS);
    config.setLockTryFrequencyMillis(LOCK_TRY_FREQ_MILLIS);
    config.setLockQuitTryingAfterMillis(LOCK_QUIT_TRY_MILLIS);

    if (throwEx != null) {
      config.setThrowExceptionIfCannotObtainLock(throwEx);
    }
    return config;
  }

  private RunnerBuilderBase runnerBuilderBaseInstance() {
    return runnerBuilderBaseInstance(null);
  }

  private RunnerBuilderBase runnerBuilderBaseInstance(ChangeLogService changeLogService) {
    return new RunnerBuilderBase(
        COMMUNITY,
        new ExecutorFactoryDefault(),
        changeLogService != null ? changeLogService : new ChangeLogService(),
        new DependencyManager(),
        new MongockConfiguration()) {


      @Override
      public RunnerBuilderBase getInstance() {
        return this;
      }


    };
  }
}

class DummyMongockConfiguration extends MongockConfiguration {

  public DummyMongockConfiguration() {
    this.setLegacyMigration(new LegacyMigrationDummyImpl());
    this.setLockRepositoryName("lockRepositoryName");
    this.setMigrationRepositoryName("changeLogRepositoryName");
  }


}

class DummyRunnerBuilder extends RunnerBuilderBase<DummyRunnerBuilder, ChangeLogItem<ChangeSetItem>, ChangeSetItem, ChangeEntry, MongockConfiguration>
    implements
    ChangeLogScanner<DummyRunnerBuilder, MongockConfiguration>,
    MigrationWriter<DummyRunnerBuilder, MongockConfiguration>,
    LegacyMigrator<DummyRunnerBuilder, MongockConfiguration>,
    DriverConnectable<DummyRunnerBuilder, ChangeEntry, MongockConfiguration>,
    Configurable<DummyRunnerBuilder, MongockConfiguration>,
    SystemVersionable<DummyRunnerBuilder, MongockConfiguration>,
    DependencyInjectable<DummyRunnerBuilder>,
    ServiceIdentificable<DummyRunnerBuilder, MongockConfiguration>,
    MongockRunnable<DummyRunnerBuilder, MongockConfiguration>,
    SelfInstanstiator<DummyRunnerBuilder> {

  protected DummyRunnerBuilder(ExecutorFactory<ChangeLogItem<ChangeSetItem>, ChangeSetItem, ChangeEntry, MongockConfiguration> executorFactory) {
    super(COMMUNITY, executorFactory, new ChangeLogService(), new DependencyManager(), new MongockConfiguration());
  }

  void validate() {
    assertEquals(driver, this.driver);
    assertFalse(config.isEnabled());
    assertEquals("start", config.getStartSystemVersion());
    assertEquals("end", config.getEndSystemVersion());
    assertFalse(config.isThrowExceptionIfCannotObtainLock());
    assertEquals(1, this.config.getChangeLogsScanPackage().size());
    assertTrue(config.getChangeLogsScanPackage().contains("package"));
  }

  @Override
  protected void beforeBuildRunner() {
  }

  @Override
  public DummyRunnerBuilder getInstance() {
    return this;
  }


  @ChangeLog
  public static class LegacyMigrationChangeLogDummy {

  }
}


