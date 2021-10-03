package io.mongock.runner.core.executor;


import io.mongock.api.ChangeLogItem;
import io.mongock.api.ChangeSetItem;
import io.mongock.api.config.LegacyMigration;
import io.mongock.api.config.LegacyMigrationMappingFields;
import io.mongock.api.config.MongockConfiguration;
import io.mongock.api.config.TransactionStrategy;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.driver.ChangeSetDependency;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.driver.api.entry.ChangeEntryService;
import io.mongock.driver.api.entry.ChangeState;
import io.mongock.driver.api.entry.ExecutedChangeEntry;
import io.mongock.driver.api.lock.LockManager;
import io.mongock.runner.core.changelogs.executor.test1.ExecutorChangeLog;
import io.mongock.runner.core.changelogs.executor.test3_with_nonFailFast.ExecutorWithNonFailFastChangeLog;
import io.mongock.runner.core.changelogs.executor.test4_with_failfast.ExecutorWithFailFastChangeLog;
import io.mongock.runner.core.changelogs.executor.test5_with_changelognonfailfast.ExecutorWithChangeLogNonFailFastChangeLog1;
import io.mongock.runner.core.changelogs.executor.test5_with_changelognonfailfast.ExecutorWithChangeLogNonFailFastChangeLog2;
import io.mongock.runner.core.changelogs.executor.test6_with_changelogfailfast.ExecutorWithChangeLogFailFastChangeLog1;
import io.mongock.runner.core.changelogs.executor.withInterfaceParameter.ChangeLogWithInterfaceParameter;
import io.mongock.runner.core.changelogs.legacymigration.LegacyMigrationChangeLog;
import io.mongock.runner.core.changelogs.skipmigration.alreadyexecuted.ChangeLogAlreadyExecuted;
import io.mongock.runner.core.changelogs.skipmigration.runalways.ChangeLogAlreadyExecutedRunAlways;
import io.mongock.runner.core.changelogs.skipmigration.withnochangeset.ChangeLogWithNoChangeSet;
import io.mongock.runner.core.changelogs.withRollback.AdvanceChangeLogWithBefore;
import io.mongock.runner.core.changelogs.withRollback.AdvanceChangeLogWithBeforeAndChangeSetFailing;
import io.mongock.runner.core.changelogs.withRollback.BasicChangeLogWithExceptionInChangeSetAndRollback;
import io.mongock.runner.core.changelogs.withRollback.BasicChangeLogWithExceptionInRollback;
import io.mongock.runner.core.executor.changelog.ChangeLogService;
import io.mongock.runner.core.executor.dependency.DependencyManager;
import io.mongock.runner.core.executor.operation.change.MigrationExecutor;
import io.mongock.runner.core.util.DummyDependencyClass;
import io.mongock.runner.core.util.InterfaceDependencyImpl;
import io.mongock.runner.core.util.InterfaceDependencyImplNoLockGarded;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.verification.Times;

import javax.inject.Named;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChangeUnitExecutorImplTest {
  private static final Function<Parameter, String> DEFAULT_PARAM_NAME_PROVIDER = parameter -> parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;
  @Rule
  public ExpectedException exceptionExpected = ExpectedException.none();
  private ChangeEntryService<ChangeEntry> changeEntryService;
  private LockManager lockManager;
  private ConnectionDriver<ChangeEntry> driver;
  private TransactionableConnectionDriver transactionableDriver;

  @Before
  public void setUp() {
    lockManager = mock(LockManager.class);
    changeEntryService = mock(ChangeEntryService.class);

    driver = mock(ConnectionDriver.class);
    when(driver.getLockManager()).thenReturn(lockManager);
    when(driver.getChangeEntryService()).thenReturn(changeEntryService);

    transactionableDriver = mock(TransactionableConnectionDriver.class);
    when(transactionableDriver.getLockManager()).thenReturn(lockManager);
    when(transactionableDriver.getChangeEntryService()).thenReturn(changeEntryService);
    when(transactionableDriver.getTransactioner()).thenReturn(Optional.of(Runnable::run));
  }

  @Test
  public void shouldRunChangeLogsSuccessfully() throws InterruptedException {
    runChangeLogsTest(false);
  }


  @Test
  public void shouldTrackIgnored_IfFlagTrackIgnored() throws InterruptedException {
    runChangeLogsTest(true);
  }

  @SuppressWarnings("unchecked")
  private void runChangeLogsTest(boolean trackingIgnored) throws InterruptedException {

    // given
    injectDummyDependency(DummyDependencyClass.class, new DummyDependencyClass());
    when(changeEntryService.getExecuted()).thenReturn(Arrays.asList(generateExecutedChangeEntry("alreadyExecuted", "executor")));

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(trackingIgnored);
    DependencyManager dm = new DependencyManager();
    new MigrationExecutor("", createInitialChangeLogsByPackage(ExecutorChangeLog.class), driver, getChangeLogRuntime(dm), config)
        .executeMigration();

    assertTrue("Changelog's methods have not been fully executed", ExecutorChangeLog.latch.await(1, TimeUnit.NANOSECONDS));
    // then
    ArgumentCaptor<ChangeEntry> captor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(trackingIgnored ? 4 : 3)).saveOrUpdate(captor.capture());

    List<ChangeEntry> entries = captor.getAllValues();
    assertEquals(trackingIgnored ? 4 : 3, entries.size());
    ChangeEntry entry = entries.get(0);
    assertEquals("newChangeSet", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));

    entry = entries.get(1);
    assertEquals("runAlwaysAndNewChangeSet", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("runAlwaysAndNewChangeSet", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));

    int nextIndex = 2;
    if (trackingIgnored) {
      entry = entries.get(nextIndex);
      assertEquals("alreadyExecuted", entry.getChangeId());
      assertEquals("executor", entry.getAuthor());
      assertEquals(ExecutorChangeLog.class.getName(), entry.getChangeLogClass());
      assertEquals("alreadyExecuted", entry.getChangeSetMethod());
      assertEquals(ChangeState.IGNORED, entry.getState());
      assertTrue(entry.getExecutionHostname().endsWith("-myService"));
      nextIndex++;
    }

    entry = entries.get(nextIndex);
    assertEquals("runAlwaysAndAlreadyExecutedChangeSet", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("runAlwaysAndAlreadyExecutedChangeSet", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));
  }

  private ChangeLogRuntimeImpl getChangeLogRuntime(DependencyManager dm) {
    return new ChangeLogRuntimeImpl(dm, DEFAULT_PARAM_NAME_PROVIDER);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldAbortMigrationButSaveFailedChangeSet_IfChangeSetThrowsException() throws InterruptedException {
    // given
    injectDummyDependency(DummyDependencyClass.class, new DummyDependencyClass());    
    when(changeEntryService.getExecuted()).thenReturn(Arrays.asList(generateExecutedChangeEntry("runAlwaysAndAlreadyExecutedChangeSet", "executor")));

    // when
    try {
      MongockConfiguration config = new MongockConfiguration();
      config.setServiceIdentifier("myService");
      config.setTrackIgnored(false);
      DependencyManager dm = new DependencyManager();
      MigrationExecutor executor = new MigrationExecutor("", createInitialChangeLogsByPackage(ExecutorWithFailFastChangeLog.class), driver,  getChangeLogRuntime(dm), config);
      executor
          .executeMigration();
    } catch (Exception ex) {
      //ignored
    }

    assertTrue("Changelog's methods have not been fully executed", ExecutorWithFailFastChangeLog.latch.await(1, TimeUnit.NANOSECONDS));
    // then
    ArgumentCaptor<ChangeEntry> captor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(3)).saveOrUpdate(captor.capture());

    List<ChangeEntry> entries = captor.getAllValues();
    assertEquals(3, entries.size());
    ChangeEntry entry = entries.get(0);
    assertEquals("newChangeSet", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithFailFastChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));

    entry = entries.get(1);
    assertEquals("runAlwaysAndNewChangeSet", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithFailFastChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("runAlwaysAndNewChangeSet", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));

    entry = entries.get(2);
    assertEquals("throwsException", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithFailFastChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("throwsException", entry.getChangeSetMethod());
    assertEquals(ChangeState.FAILED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldThrowException_ifNoArgumentFound() {
    // given
    when(changeEntryService.getExecuted()).thenReturn(Collections.emptyList());
    
    // then
    exceptionExpected.expect(MongockException.class);
    exceptionExpected.expectMessage("Error in method[ExecutorChangeLog.newChangeSet] : Wrong parameter[DummyDependencyClass]");

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    DependencyManager dm = new DependencyManager();
    new MigrationExecutor("", createInitialChangeLogsByPackage(ExecutorChangeLog.class), driver, getChangeLogRuntime(dm), config)
        .executeMigration();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldThrowException_ifWrongArgument() {
    // given
    injectDummyDependency(DummyDependencyClass.class, "Wrong parameter");
    when(changeEntryService.getExecuted()).thenReturn(Collections.emptyList());

    //then
    exceptionExpected.expect(MongockException.class);
    exceptionExpected.expectMessage("argument type mismatch");

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    DependencyManager dm = new DependencyManager();
    new MigrationExecutor("", createInitialChangeLogsByPackage(ExecutorChangeLog.class), driver, getChangeLogRuntime(dm), config)
        .executeMigration();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldCloseLockManager_WhenException() {
    // given
    injectDummyDependency(DummyDependencyClass.class, "Wrong parameter");
    when(changeEntryService.getExecuted()).thenReturn(Collections.emptyList());

    // when
    try {
      MongockConfiguration config = new MongockConfiguration();
      config.setServiceIdentifier("myService");
      config.setTrackIgnored(false);
      DependencyManager dm = new DependencyManager();
      new MigrationExecutor("", createInitialChangeLogsByPackage(ExecutorChangeLog.class), driver, getChangeLogRuntime(dm), config)
          .executeMigration();
    } catch (Exception ex) {
    }

    //then
    verify(lockManager, new Times(1)).close();
  }


  @Test(expected = MongockException.class)
  @SuppressWarnings("unchecked")
  public void shouldPropagateMongockException_EvenWhenThrowExIfCannotLock_IfDriverNotValidated() {
    // given
    injectDummyDependency(DummyDependencyClass.class, "Wrong parameter");
    when(changeEntryService.getExecuted()).thenReturn(Collections.emptyList());
    doThrow(MongockException.class).when(driver).runValidation();

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    DependencyManager dm = new DependencyManager();
    new MigrationExecutor("", createInitialChangeLogsByPackage(ExecutorChangeLog.class), driver, getChangeLogRuntime(dm), config)
        .executeMigration();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldContinueMigration_whenAChangeSetFails_ifChangeSetIsNonFailFast() throws InterruptedException {
    // given
    injectDummyDependency(DummyDependencyClass.class, new DummyDependencyClass());
    when(changeEntryService.getExecuted()).thenReturn(Collections.emptyList());

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    DependencyManager dm = new DependencyManager();
    new MigrationExecutor("", createInitialChangeLogsByPackage(ExecutorWithNonFailFastChangeLog.class), driver, getChangeLogRuntime(dm), config)
        .executeMigration();

    assertTrue("Changelog's methods have not been fully executed", ExecutorWithNonFailFastChangeLog.latch.await(1, TimeUnit.NANOSECONDS));
    // then
    ArgumentCaptor<ChangeEntry> captor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(3)).saveOrUpdate(captor.capture());

    List<ChangeEntry> entries = captor.getAllValues();
    assertEquals(3, entries.size());

    ChangeEntry entry = entries.get(0);
    assertEquals("newChangeSet1", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithNonFailFastChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet1", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));


    entry = entries.get(1);
    assertEquals("changeSetNonFailFast", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithNonFailFastChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("changeSetNonFailFast", entry.getChangeSetMethod());
    assertEquals(ChangeState.FAILED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));

    entry = entries.get(2);
    assertEquals("newChangeSet2", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithNonFailFastChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet2", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldContinueMigration_whenAChangeSetFails_ifChangeLogIsNonFailFast() throws InterruptedException {
    // given
    injectDummyDependency(DummyDependencyClass.class, new DummyDependencyClass());
    when(changeEntryService.getExecuted()).thenReturn(Collections.emptyList());

    // when
    try {
      MongockConfiguration config = new MongockConfiguration();
      config.setServiceIdentifier("myService");
      config.setTrackIgnored(false);
      DependencyManager dm = new DependencyManager();
      new MigrationExecutor("", createInitialChangeLogsByPackage(ExecutorWithChangeLogNonFailFastChangeLog1.class), driver, getChangeLogRuntime(dm), config)
          .executeMigration();
    } catch (Exception ex) {
    }

    assertTrue("Changelog's (1) methods have not been fully executed", ExecutorWithChangeLogNonFailFastChangeLog1.latch.await(1, TimeUnit.NANOSECONDS));
    assertTrue("Changelog's (2) methods have not been fully executed", ExecutorWithChangeLogNonFailFastChangeLog2.latch.await(1, TimeUnit.NANOSECONDS));

    // then
    ArgumentCaptor<ChangeEntry> captor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(4)).saveOrUpdate(captor.capture());

    List<ChangeEntry> entries = captor.getAllValues();
    assertEquals(4, entries.size());

    ChangeEntry entry = entries.get(0);
    assertEquals("newChangeSet11", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithChangeLogNonFailFastChangeLog1.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet11", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));

    entry = entries.get(1);
    assertEquals("newChangeSet12", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithChangeLogNonFailFastChangeLog1.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet12", entry.getChangeSetMethod());
    assertEquals(ChangeState.FAILED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));

    entry = entries.get(2);
    assertEquals("newChangeSet21", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithChangeLogNonFailFastChangeLog2.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet21", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));

    entry = entries.get(3);
    assertEquals("newChangeSet22", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithChangeLogNonFailFastChangeLog2.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet22", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldAbortMigration_whenAChangeSetFails_ifChangeLogIsFailFast() throws InterruptedException {
    // given
    injectDummyDependency(DummyDependencyClass.class, new DummyDependencyClass());
    when(changeEntryService.getExecuted()).thenReturn(Collections.emptyList());

    // when
    try {
      MongockConfiguration config = new MongockConfiguration();
      config.setServiceIdentifier("myService");
      config.setTrackIgnored(false);
      DependencyManager dm = new DependencyManager();
      new MigrationExecutor("", createInitialChangeLogsByPackage(ExecutorWithChangeLogFailFastChangeLog1.class), driver, getChangeLogRuntime(dm), config)
          .executeMigration();
    } catch (Exception ex) {
    }

    assertTrue("Changelog's methods have not been fully executed", ExecutorWithChangeLogFailFastChangeLog1.latch.await(1, TimeUnit.NANOSECONDS));

    // then
    ArgumentCaptor<ChangeEntry> captor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(2)).saveOrUpdate(captor.capture());

    List<ChangeEntry> entries = captor.getAllValues();
    assertEquals(2, entries.size());

    ChangeEntry entry = entries.get(0);
    assertEquals("newChangeSet11", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithChangeLogFailFastChangeLog1.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet11", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));

    entry = entries.get(1);
    assertEquals("newChangeSet12", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithChangeLogFailFastChangeLog1.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet12", entry.getChangeSetMethod());
    assertEquals(ChangeState.FAILED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));
  }


  @Test
  @SuppressWarnings("unchecked")
  public void shouldReturnProxy_IfStandardDependency() {
    // given
    when(changeEntryService.getExecuted()).thenReturn(Arrays.asList(
            generateExecutedChangeEntry("withInterfaceParameter2", "executor"),
            generateExecutedChangeEntry("withNonLockGuardedParameter", "executor")
    ));

    // when
    when(driver.getLockManager()).thenReturn(lockManager);
    DependencyManager dm = new DependencyManager()
        .addStandardDependency(new ChangeSetDependency(new InterfaceDependencyImpl()));

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor("", createInitialChangeLogsByPackage(ChangeLogWithInterfaceParameter.class), driver, getChangeLogRuntime(dm), config)
        .executeMigration();

    // then
    verify(lockManager, new Times(1)).ensureLockDefault();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void proxyReturnedShouldReturnAProxy_whenCallingAMethod_IfInterface() {
    // given
    when(changeEntryService.getExecuted()).thenReturn(Arrays.asList(
            generateExecutedChangeEntry("withInterfaceParameter", "executor"),
            generateExecutedChangeEntry("withNonLockGuardedParameter", "executor")
    ));

    // when
    when(driver.getLockManager()).thenReturn(lockManager);
    DependencyManager dm = new DependencyManager()
        .addStandardDependency(new ChangeSetDependency(new InterfaceDependencyImpl()));

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor("", createInitialChangeLogsByPackage(ChangeLogWithInterfaceParameter.class), driver, getChangeLogRuntime(dm), config)
        .executeMigration();

    // then
    verify(lockManager, new Times(2)).ensureLockDefault();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldNotReturnProxy_IfClassAnnotatedWithNonLockGuarded() {
    // given
    when(changeEntryService.getExecuted()).thenReturn(Arrays.asList(
            generateExecutedChangeEntry("withInterfaceParameter2", "executor"),
            generateExecutedChangeEntry("withNonLockGuardedParameter", "executor")
    ));

    // when
    when(driver.getLockManager()).thenReturn(lockManager);
    DependencyManager dm = new DependencyManager()
        .addStandardDependency(new ChangeSetDependency(new InterfaceDependencyImplNoLockGarded()));

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor("", createInitialChangeLogsByPackage(ChangeLogWithInterfaceParameter.class), driver, getChangeLogRuntime(dm), config)
        .executeMigration();

    // then
    verify(lockManager, new Times(0)).ensureLockDefault();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldNotReturnProxy_IfParameterAnnotatedWithNonLockGuarded() {
    // given
    when(changeEntryService.getExecuted()).thenReturn(Arrays.asList(
            generateExecutedChangeEntry("withInterfaceParameter", "executor"),
            generateExecutedChangeEntry("withInterfaceParameter2", "executor")
    ));

    // when
    when(driver.getLockManager()).thenReturn(lockManager);
    DependencyManager dm = new DependencyManager()
        .addStandardDependency(new ChangeSetDependency(new InterfaceDependencyImpl()));

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor("", createInitialChangeLogsByPackage(ChangeLogWithInterfaceParameter.class), driver, getChangeLogRuntime(dm), config)
        .executeMigration();

    // then
    verify(lockManager, new Times(0)).ensureLockDefault();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldInjectLegacyMigrationList_whenNamed() throws InterruptedException {
    // given
    when(changeEntryService.getExecuted()).thenReturn(Collections.emptyList());
    
    // when
    when(driver.getLockManager()).thenReturn(lockManager);
    LegacyMigrationMappingFields mappingFields = new LegacyMigrationMappingFields();
    mappingFields.setAuthor("AUTHOR");
    LegacyMigration dependency = new LegacyMigration() {
    };
    dependency.setMappingFields(mappingFields);
    DependencyManager dm = new DependencyManager()
        .addStandardDependency(new ChangeSetDependency(List.class, Collections.singletonList(new LegacyMigration() {
        })))
        .addStandardDependency(new ChangeSetDependency("legacyMigration2", List.class, Collections.singletonList(new LegacyMigration() {
        })))
        .addStandardDependency(new ChangeSetDependency("legacyMigration", List.class, Collections.singletonList(dependency)))
        .addStandardDependency(new ChangeSetDependency(List.class, Collections.singletonList(new LegacyMigration() {
        })))
        .addStandardDependency(new ChangeSetDependency("legacyMigration3", List.class, Collections.singletonList(new LegacyMigration() {
        })));

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor("", createInitialChangeLogsByPackage(LegacyMigrationChangeLog.class), driver, getChangeLogRuntime(dm), config)
        .executeMigration();

    // then
    LegacyMigrationChangeLog.latch.await(5, TimeUnit.SECONDS);
  }

  @Test
  public void shouldSkipMigration_whenChangeLogWithNoChangeSet() {
    // given
    
    when(driver.getLockManager()).thenReturn(lockManager);
    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    DependencyManager dm = new DependencyManager();
    new MigrationExecutor("", createInitialChangeLogsByPackage(ChangeLogWithNoChangeSet.class), driver, getChangeLogRuntime(dm), config)
        .executeMigration();

    //then
    ArgumentCaptor<String> changeSetIdCaptor = ArgumentCaptor.forClass(String.class);
    // Lock should not be acquired because there is no change set.
    verify(lockManager, new Times(0)).acquireLockDefault();
  }

  @Test
  public void shouldSkipMigration_whenAllChangeSetItemsAlreadyExecuted() {
    // given
    when(changeEntryService.getExecuted()).thenReturn(Arrays.asList(
            generateExecutedChangeEntry("alreadyExecuted", "executor"),
            generateExecutedChangeEntry("alreadyExecuted2", "executor")
    ));

    when(driver.getLockManager()).thenReturn(lockManager);

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    DependencyManager dm = new DependencyManager();
    new MigrationExecutor("", createInitialChangeLogsByPackage(ChangeLogAlreadyExecuted.class), driver, getChangeLogRuntime(dm), config)
        .executeMigration();

    //then
    // Lock should not be acquired because all items are already executed.
    verify(lockManager, new Times(0)).acquireLockDefault();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldStoreChangeLog_whenRunAlways_ifNotAlreadyExecuted() {
    // given
    injectDummyDependency(DummyDependencyClass.class, new DummyDependencyClass());
    when(changeEntryService.getExecuted()).thenReturn(Arrays.asList(
            generateExecutedChangeEntry("alreadyExecuted", "executor"),
            generateExecutedChangeEntry("alreadyExecuted2", "executor")
    ));

    when(driver.getLockManager()).thenReturn(lockManager);

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    DependencyManager dm = new DependencyManager();
    new MigrationExecutor("", createInitialChangeLogsByPackage(ChangeLogAlreadyExecutedRunAlways.class), driver, getChangeLogRuntime(dm), config)
        .executeMigration();

    //then
    verify(lockManager, new Times(1)).acquireLockDefault();

    ArgumentCaptor<ChangeEntry> changeEntryCaptor = ArgumentCaptor.forClass(ChangeEntry.class);
    // ChangeEntry for ChangeSet "alreadyExecutedRunAlways" should be stored
    verify(changeEntryService, new Times(1)).saveOrUpdate(changeEntryCaptor.capture());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldNotStoreChangeLog_whenRunAlways_ifAlreadyExecuted() {
    // given
    injectDummyDependency(DummyDependencyClass.class, new DummyDependencyClass());
    when(changeEntryService.getExecuted()).thenReturn(Arrays.asList(
            generateExecutedChangeEntry("alreadyExecuted", "executor"),
            generateExecutedChangeEntry("alreadyExecuted2", "executor"),
            generateExecutedChangeEntry("alreadyExecutedRunAlways", "executor")
    ));

    when(driver.getLockManager()).thenReturn(lockManager);

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    DependencyManager dm = new DependencyManager();
    new MigrationExecutor("", createInitialChangeLogsByPackage(ChangeLogAlreadyExecutedRunAlways.class), driver, getChangeLogRuntime(dm), config)
        .executeMigration();

    //then
    verify(lockManager, new Times(1)).acquireLockDefault();

    ArgumentCaptor<ChangeEntry> changeEntryCaptor = ArgumentCaptor.forClass(ChangeEntry.class);
    // ChangeEntry for ChangeSet "alreadyExecutedRunAlways" should not be stored
    verify(changeEntryService, new Times(0)).saveOrUpdate(changeEntryCaptor.capture());
  }

  @Test
  public void SHOULDRollback_WHENBasicChangeSetFails_IFNoTransactional() throws InterruptedException {
    // given
    when(transactionableDriver.getLockManager()).thenReturn(lockManager);

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);

    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBaseClassList(Collections.singletonList(BasicChangeLogWithExceptionInChangeSetAndRollback.class));

    Assert.assertThrows(
        MongockException.class,
        () -> {
          DependencyManager dm = new DependencyManager();
          new MigrationExecutor("", changeLogService.fetchChangeLogs(), driver, getChangeLogRuntime(dm), config)
              .executeMigration();
        });

    assertTrue("Rollback method wasn't executed", BasicChangeLogWithExceptionInChangeSetAndRollback.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS));

    ArgumentCaptor<ChangeEntry> changeEntryCaptor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(2))
        .saveOrUpdate(changeEntryCaptor.capture());

    List<ChangeEntry> allValues = changeEntryCaptor.getAllValues();
    assertEquals(ChangeState.FAILED, allValues.get(0).getState());
    assertEquals("changeset_with_exception_and_rollback_1", allValues.get(0).getChangeId());
    assertEquals(ChangeState.ROLLED_BACK, allValues.get(1).getState());
    assertEquals("changeset_with_exception_and_rollback_1", allValues.get(1).getChangeId());
  }

  @Test
  public void shouldReturnFailedChangeEntry_whenRollback_ifThrowsRollbackThrowsException() throws InterruptedException {
    // given
    when(transactionableDriver.getLockManager()).thenReturn(lockManager);

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);

    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBaseClassList(Collections.singletonList(BasicChangeLogWithExceptionInRollback.class));

    Assert.assertThrows(
        MongockException.class,
        () -> {
          DependencyManager dm = new DependencyManager();
          new MigrationExecutor("", changeLogService.fetchChangeLogs(), driver, getChangeLogRuntime(dm), config)
              .executeMigration();
        });

    ArgumentCaptor<ChangeEntry> changeEntryCaptor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(2)).saveOrUpdate(changeEntryCaptor.capture());

    List<ChangeEntry> changeEntryList = changeEntryCaptor.getAllValues();
    assertEquals("changeset_with_exception_in_rollback_1", changeEntryList.get(0).getChangeId());
    assertEquals(ChangeState.FAILED, changeEntryList.get(0).getState());

    assertEquals("changeset_with_exception_in_rollback_1", changeEntryList.get(1).getChangeId());
    assertEquals(ChangeState.ROLLBACK_FAILED, changeEntryList.get(1).getState());

  }

  @Test
  public void shouldRollbackManuallyAllChangeSetsAndStoreChangeEntry_whenSecondChangeLogFailAtChangeSet_ifStrategyIsMigrationAndNonTransactional() throws InterruptedException {
    // given
    when(transactionableDriver.getLockManager()).thenReturn(lockManager);

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    config.setTransactionStrategy(TransactionStrategy.EXECUTION);

    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBaseClassList(Arrays.asList(AdvanceChangeLogWithBefore.class, AdvanceChangeLogWithBeforeAndChangeSetFailing.class));

    when(driver.isTransactionable()).thenReturn(false);
    Assert.assertThrows(
        MongockException.class,
        () -> {
          DependencyManager dm = new DependencyManager();
          new MigrationExecutor("", changeLogService.fetchChangeLogs(), driver, getChangeLogRuntime(dm), config)
              .executeMigration();
        });


    // checks the four rollbacks were called
    assertTrue("AdvanceChangeLogWithBefore's Rollback method wasn't executed", AdvanceChangeLogWithBefore.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS));
    assertTrue("AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed", AdvanceChangeLogWithBeforeAndChangeSetFailing.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS));

    ArgumentCaptor<ChangeEntry> changeEntryCaptor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(8)).saveOrUpdate(changeEntryCaptor.capture());

    List<ChangeEntry> changeEntryList = changeEntryCaptor.getAllValues();
    assertEquals("AdvanceChangeLogWithBefore_before", changeEntryList.get(0).getChangeId());
    assertEquals(ChangeState.EXECUTED, changeEntryList.get(0).getState());
    assertEquals("AdvanceChangeLogWithBefore", changeEntryList.get(1).getChangeId());
    assertEquals(ChangeState.EXECUTED, changeEntryList.get(1).getState());
    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing_before", changeEntryList.get(2).getChangeId());
    assertEquals(ChangeState.EXECUTED, changeEntryList.get(2).getState());
    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing", changeEntryList.get(3).getChangeId());
    assertEquals(ChangeState.FAILED, changeEntryList.get(3).getState());

    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing", changeEntryList.get(4).getChangeId());
    assertEquals(ChangeState.ROLLED_BACK, changeEntryList.get(4).getState());
    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing_before", changeEntryList.get(5).getChangeId());
    assertEquals(ChangeState.ROLLED_BACK, changeEntryList.get(5).getState());
    assertEquals("AdvanceChangeLogWithBefore", changeEntryList.get(6).getChangeId());
    assertEquals(ChangeState.ROLLED_BACK, changeEntryList.get(6).getState());
    assertEquals("AdvanceChangeLogWithBefore_before", changeEntryList.get(7).getChangeId());
    assertEquals(ChangeState.ROLLED_BACK, changeEntryList.get(7).getState());

  }

  @Test
  public void shouldRollbackManuallyOnlyChangeSetsOfLastChangelogAndStoreChangeEntry_whenSecondChangeLogFailAtChangeSet_ifStrategyIsChangeLogAndNonTransactional() throws InterruptedException {
    // given
    when(transactionableDriver.getLockManager()).thenReturn(lockManager);

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);

    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBaseClassList(Arrays.asList(AdvanceChangeLogWithBefore.class, AdvanceChangeLogWithBeforeAndChangeSetFailing.class));

    when(driver.isTransactionable()).thenReturn(false);
    Assert.assertThrows(
        MongockException.class,
        () -> {
          DependencyManager dm = new DependencyManager();
          new MigrationExecutor("", changeLogService.fetchChangeLogs(), driver, getChangeLogRuntime(dm), config)
              .executeMigration();
        });


    // checks the four rollbacks were called
    assertTrue("AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed", AdvanceChangeLogWithBeforeAndChangeSetFailing.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS));

    ArgumentCaptor<ChangeEntry> changeEntryCaptor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(6)).saveOrUpdate(changeEntryCaptor.capture());

    List<ChangeEntry> changeEntryList = changeEntryCaptor.getAllValues();
    assertEquals("AdvanceChangeLogWithBefore_before", changeEntryList.get(0).getChangeId());
    assertEquals(ChangeState.EXECUTED, changeEntryList.get(0).getState());
    assertEquals("AdvanceChangeLogWithBefore", changeEntryList.get(1).getChangeId());
    assertEquals(ChangeState.EXECUTED, changeEntryList.get(1).getState());
    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing_before", changeEntryList.get(2).getChangeId());
    assertEquals(ChangeState.EXECUTED, changeEntryList.get(2).getState());
    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing", changeEntryList.get(3).getChangeId());
    assertEquals(ChangeState.FAILED, changeEntryList.get(3).getState());

    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing", changeEntryList.get(4).getChangeId());
    assertEquals(ChangeState.ROLLED_BACK, changeEntryList.get(4).getState());
    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing_before", changeEntryList.get(5).getChangeId());
    assertEquals(ChangeState.ROLLED_BACK, changeEntryList.get(5).getState());
  }

  @Test
  public void shouldNotRollbackManuallyAnyChangeSetsAndStoreChangeEntries_whenSecondChangeLogFailAtChangeSet_ifStrategyIsMigrationAndTransactional() throws InterruptedException {
    // given
    when(transactionableDriver.getLockManager()).thenReturn(lockManager);

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    config.setTransactionStrategy(TransactionStrategy.EXECUTION);
    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBaseClassList(Arrays.asList(AdvanceChangeLogWithBefore.class, AdvanceChangeLogWithBeforeAndChangeSetFailing.class));

    when(driver.isTransactionable()).thenReturn(true);

    Assert.assertThrows(
        MongockException.class,
        () -> {
          DependencyManager dm = new DependencyManager();
          new MigrationExecutor("", changeLogService.fetchChangeLogs(), driver, getChangeLogRuntime(dm), config)
              .executeMigration();
        });


    // checks the four rollbacks were called
    assertFalse("AdvanceChangeLogWithBefore's Rollback before method wasn't executed", AdvanceChangeLogWithBefore.rollbackBeforeCalled);
    assertFalse("AdvanceChangeLogWithBefore's Rollback method wasn't executed", AdvanceChangeLogWithBefore.rollbackCalled);

    assertFalse("AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback before method wasn't executed", AdvanceChangeLogWithBeforeAndChangeSetFailing.rollbackBeforeCalled);
    assertFalse("AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed", AdvanceChangeLogWithBeforeAndChangeSetFailing.rollbackCalled);

    ArgumentCaptor<ChangeEntry> changeEntryCaptor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(4)).saveOrUpdate(changeEntryCaptor.capture());

    List<ChangeEntry> changeEntryList = changeEntryCaptor.getAllValues();
    assertEquals("AdvanceChangeLogWithBefore_before", changeEntryList.get(0).getChangeId());
    assertEquals(ChangeState.EXECUTED, changeEntryList.get(0).getState());
    assertEquals("AdvanceChangeLogWithBefore", changeEntryList.get(1).getChangeId());
    assertEquals(ChangeState.EXECUTED, changeEntryList.get(1).getState());
    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing_before", changeEntryList.get(2).getChangeId());
    assertEquals(ChangeState.EXECUTED, changeEntryList.get(2).getState());
    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing", changeEntryList.get(3).getChangeId());
    assertEquals(ChangeState.FAILED, changeEntryList.get(3).getState());
    //the driver is in charge of rolling back the changes and the changeEntries

  }

  @Test
  public void sshouldNotRollbackManuallyAnyChangeSetsAndStoreChangeEntries_whenSecondChangeLogFailAtChangeSet_ifStrategyIsMigrationAndTransactional() throws InterruptedException {
    // given
    when(transactionableDriver.getLockManager()).thenReturn(lockManager);

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    config.setTransactionStrategy(TransactionStrategy.CHANGE_UNIT);

    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBaseClassList(Arrays.asList(AdvanceChangeLogWithBefore.class, AdvanceChangeLogWithBeforeAndChangeSetFailing.class));

    when(driver.isTransactionable()).thenReturn(true);
    Assert.assertThrows(
        MongockException.class,
        () -> {
          DependencyManager dm = new DependencyManager();
          new MigrationExecutor("", changeLogService.fetchChangeLogs(), driver, getChangeLogRuntime(dm), config)
              .executeMigration();
        });


    // checks the four rollbacks were called
    assertFalse("AdvanceChangeLogWithBefore's Rollback before method wasn't executed", AdvanceChangeLogWithBefore.rollbackBeforeCalled);
    assertFalse("AdvanceChangeLogWithBefore's Rollback method wasn't executed", AdvanceChangeLogWithBefore.rollbackCalled);

    assertTrue("AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback before method wasn't executed", AdvanceChangeLogWithBeforeAndChangeSetFailing.rollbackBeforeCalled);
    assertFalse("AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed", AdvanceChangeLogWithBeforeAndChangeSetFailing.rollbackCalled);

    ArgumentCaptor<ChangeEntry> changeEntryCaptor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(5)).saveOrUpdate(changeEntryCaptor.capture());

    List<ChangeEntry> changeEntryList = changeEntryCaptor.getAllValues();
    assertEquals("AdvanceChangeLogWithBefore_before", changeEntryList.get(0).getChangeId());
    assertEquals(ChangeState.EXECUTED, changeEntryList.get(0).getState());
    assertEquals("AdvanceChangeLogWithBefore", changeEntryList.get(1).getChangeId());
    assertEquals(ChangeState.EXECUTED, changeEntryList.get(1).getState());
    //the previous ones are not roll back as the strategy is changelog

    //this is the only one that should be rolled back
    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing_before", changeEntryList.get(2).getChangeId());
    assertEquals(ChangeState.EXECUTED, changeEntryList.get(2).getState());

    //This is rolled back automatically by the transaction,
    //so the changeEntry is not saved as rolled_back, because the transaction itself is rolled back and  removed
    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing", changeEntryList.get(3).getChangeId());
    assertEquals(ChangeState.FAILED, changeEntryList.get(3).getState());

    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing_before", changeEntryList.get(4).getChangeId());
    assertEquals(ChangeState.ROLLED_BACK, changeEntryList.get(4).getState());

  }

  private SortedSet<ChangeLogItem<ChangeSetItem>> createInitialChangeLogsByPackage(Class<?>... executorChangeLogClass) {
    List<String> packages = Stream.of(executorChangeLogClass)
        .map(clazz -> clazz.getPackage().getName())
        .collect(Collectors.toList());
    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBasePackageList(packages);
    return changeLogService.fetchChangeLogs();

  }


  private void injectDummyDependency(Class<?> type, Object instance) {
    Set<ChangeSetDependency> dependencies = new HashSet<>();
    dependencies.add(new ChangeSetDependency(type, instance));
    when(driver.getDependencies()).thenReturn(dependencies);
  }

  private abstract static class TransactionableConnectionDriver implements ConnectionDriver<ChangeEntry> {
  }

  private ExecutedChangeEntry generateExecutedChangeEntry(String changeId, String author) {
    return new ExecutedChangeEntry(changeId, author, new Date(), "dummy", "dummy");
  }
}
