package io.mongock.runner.springboot;


import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.driver.ChangeSetDependency;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.driver.api.entry.ChangeEntryService;
import io.mongock.driver.api.entry.ExecutedChangeEntry;
import io.mongock.driver.api.lock.LockManager;
import io.mongock.runner.springboot.profiles.enseuredecorators.EnsureDecoratorChangerLog;
import io.mongock.runner.springboot.profiles.integration.IntegrationProfiledChangerLog;
import io.mongock.runner.springboot.profiles.withinterfaceparameter.ChangeLogWithInterfaceParameter;
import io.mongock.runner.springboot.util.CallVerifier;
import io.mongock.runner.springboot.util.ClassNotInterfaced;
import io.mongock.runner.springboot.util.InterfaceDependency;
import io.mongock.runner.springboot.util.InterfaceDependencyImpl;
import io.mongock.runner.springboot.util.InterfaceDependencyImplNoLockGarded;
import io.mongock.runner.springboot.util.TemplateForTest;
import io.mongock.runner.springboot.util.TemplateForTestImpl;
import io.mongock.runner.springboot.util.TemplateForTestImplChild;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.internal.verification.Times;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SpringMongockApplicationRunnerBaseTest {

  @Rule
  public ExpectedException exceptionExpected = ExpectedException.none();
  private ChangeEntryService<ChangeEntry> changeEntryService;
  private LockManager lockManager;
  private ConnectionDriver<ChangeEntry> driver;
  private CallVerifier callVerifier;
  private ApplicationContext springContext;

  @Before
  public void setUp() {
    lockManager = mock(LockManager.class);
    changeEntryService = mock(ChangeEntryService.class);
    driver = mock(ConnectionDriver.class);
    when(driver.getLockManager()).thenReturn(lockManager);
    when(driver.getChangeEntryService()).thenReturn(changeEntryService);

    callVerifier = new CallVerifier();
    Set<ChangeSetDependency> dependencySet = new HashSet<>();
    dependencySet.add(new ChangeSetDependency(CallVerifier.class, callVerifier));
    when(driver.getDependencies()).thenReturn(dependencySet);


    Environment environment = mock(Environment.class);
    when(environment.getActiveProfiles()).thenReturn(new String[]{"profileIncluded1", "profileIncluded2"});
    springContext = mock(ApplicationContext.class);
    when(springContext.getEnvironment()).thenReturn(environment);
    when(springContext.getBean(Environment.class)).thenReturn(environment);
    when(springContext.getBean(TemplateForTest.class)).thenReturn(new TemplateForTestImpl());
    when(springContext.getBean(InterfaceDependency.class)).thenReturn(new InterfaceDependencyImpl());
    when(springContext.getBean(ClassNotInterfaced.class)).thenReturn(new ClassNotInterfaced());
  }

  //TODO: Change this tests because we can't check the requirement with isAlreadyExecuted method (not called now)
  /*@Test
  public void shouldRunOnlyProfiledChangeSets() throws Exception {

    // when
    buildAndRun(IntegrationProfiledChangerLog.class.getPackage().getName());

    // then
    ArgumentCaptor<String> changeSetIdCaptor = ArgumentCaptor.forClass(String.class);
    int wantedNumberOfInvocations = 3 + 1; // 3 -> Number of changes, 1 -> Pre migration check
    verify(changeEntryService, new Times(wantedNumberOfInvocations)).isAlreadyExecuted(changeSetIdCaptor.capture(), anyString());

    List<String> changeSetIdList = changeSetIdCaptor.getAllValues();
    assertEquals(wantedNumberOfInvocations, changeSetIdList.size());
    assertEquals(2, Collections.frequency(changeSetIdList, "testWithProfileIncluded1"));
    assertTrue(changeSetIdList.contains("testWithProfileIncluded2"));
    assertTrue(changeSetIdList.contains("testWithProfileIncluded1OrProfileINotIncluded"));
  }*/

  @Test
  public void shouldInjectEnvironmentToChangeSet() throws Exception {
    // when
    buildAndRun(IntegrationProfiledChangerLog.class.getPackage().getName());

    // then
    assertEquals(1, callVerifier.counter);
  }

  @Test
  public void shouldPrioritizeConnectorDependenciesOverContext() throws Exception {
    // given
    when(changeEntryService.getExecuted()).thenReturn(Collections.emptyList());
    callVerifier = new CallVerifier();
    Set<ChangeSetDependency> dependencySet = new HashSet<>();
    dependencySet.add(new ChangeSetDependency(CallVerifier.class, callVerifier));
    dependencySet.add(new ChangeSetDependency(TemplateForTestImpl.class, new TemplateForTestImplChild()));
    when(driver.getDependencies()).thenReturn(dependencySet);

    Environment environment = mock(Environment.class);
    springContext = mock(ApplicationContext.class);
    when(springContext.getEnvironment()).thenReturn(environment);
    when(springContext.getBean(Environment.class)).thenReturn(environment);
    when(springContext.getBean(TemplateForTestImpl.class)).thenReturn(new TemplateForTestImpl());

    // when
    buildAndRun(EnsureDecoratorChangerLog.class.getPackage().getName());

    // then
    assertEquals(1, callVerifier.counter);
  }

  @Test
  public void shouldFail_IfSpringContextNotInjected() throws Exception {

    exceptionExpected.expect(MongockException.class);
    exceptionExpected.expectMessage("ApplicationContext from Spring must be injected to Builder");

    MongockSpringboot.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(IntegrationProfiledChangerLog.class.getPackage().getName())
        .buildApplicationRunner()
        .run(null);
  }

  @Test
  public void shouldReturnProxy_IfStandardDependency() throws Exception {
    // given
    when(changeEntryService.getExecuted()).thenReturn(Arrays.asList(
            generateExecutedChangeEntry("withInterfaceParameter2", "executor"),
            generateExecutedChangeEntry("withClassNotInterfacedParameter", "executor")
    ));

    // when
    buildAndRun(ChangeLogWithInterfaceParameter.class.getPackage().getName());

    // then
    verify(lockManager, new Times(1)).ensureLockDefault();
  }

  @Test
  public void proxyReturnedShouldReturnAProxy_whenCallingAMethod_IfInterface() throws Exception {
    // given
    when(changeEntryService.getExecuted()).thenReturn(Arrays.asList(
            generateExecutedChangeEntry("withInterfaceParameter", "executor"),
            generateExecutedChangeEntry("withClassNotInterfacedParameter", "executor")
    ));

    // when
    buildAndRun(ChangeLogWithInterfaceParameter.class.getPackage().getName());

    // then
    verify(lockManager, new Times(2)).ensureLockDefault();
  }


  @Test
  public void shouldNotReturnProxy_IfClassAnnotatedWithNonLockGuarded() throws Exception {
    // given
    when(changeEntryService.getExecuted()).thenReturn(Arrays.asList(
            generateExecutedChangeEntry("withInterfaceParameter2", "executor"),
            generateExecutedChangeEntry("withClassNotInterfacedParameter", "executor")
    ));
    when(springContext.getBean(InterfaceDependency.class)).thenReturn(new InterfaceDependencyImplNoLockGarded());


    // when
    buildAndRun(ChangeLogWithInterfaceParameter.class.getPackage().getName());

    // then
    verify(lockManager, new Times(0)).ensureLockDefault();
  }

  @Test
  public void shouldNotReturnProxy_IfParameterAnnotatedWithNonLockGuarded() throws Exception {
    // given
    when(changeEntryService.getExecuted()).thenReturn(Arrays.asList(
            generateExecutedChangeEntry("withInterfaceParameter", "executor"),
            generateExecutedChangeEntry("withInterfaceParameter2", "executor"),
            generateExecutedChangeEntry("withClassNotInterfacedParameter", "executor")
    ));

    // when
    buildAndRun(ChangeLogWithInterfaceParameter.class.getPackage().getName());

    // then
    verify(lockManager, new Times(0)).ensureLockDefault();
  }

  private void buildAndRun(String packageName) throws Exception {
    MongockSpringboot
        .builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(packageName)
        .setSpringContext(springContext)
        .buildApplicationRunner()
        .run(null);

  }
  
  private ExecutedChangeEntry generateExecutedChangeEntry(String changeId, String author) {
    return new ExecutedChangeEntry(changeId, author, new Date(), "dummy", "dummy");
  }
}
