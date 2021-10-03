package io.mongock.runner.springboot;


import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.driver.ChangeSetDependency;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.driver.api.entry.ChangeEntryService;
import io.mongock.driver.api.lock.LockManager;
import io.mongock.runner.springboot.profiles.enseuredecorators.EnsureDecoratorChangerLog;
import io.mongock.runner.springboot.profiles.integration.IntegrationProfiledChangerLog;
import io.mongock.runner.springboot.util.CallVerifier;
import io.mongock.runner.springboot.util.TemplateForTest;
import io.mongock.runner.springboot.util.TemplateForTestImpl;
import io.mongock.runner.springboot.util.TemplateForTestImplChild;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpringMongockInitializingBeanRunnerBaseTest {

  @Rule
  public ExpectedException exceptionExpected = ExpectedException.none();
  private ChangeEntryService<ChangeEntry> changeEntryService;
  private ConnectionDriver<ChangeEntry> driver;
  private CallVerifier callVerifier;
  private ApplicationContext springContext;

  @Before
  public void setUp() {
    LockManager lockManager = mock(LockManager.class);
    changeEntryService = mock(ChangeEntryService.class);
    driver = mock(ConnectionDriver.class);
    when(driver.getLockManager()).thenReturn(lockManager);
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
    when(springContext.getBean(TemplateForTest.class))
        .thenReturn(new TemplateForTestImpl());
  }

  //TODO: Change this tests because we can't check the requirement with isAlreadyExecuted method (not called now)
  /*@Test
  public void shouldRunOnlyProfiledChangeSets() throws Exception {

    // when
//        Spring5Runner.migrationBuilder()
    MongockSpringboot.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(IntegrationProfiledChangerLog.class.getPackage().getName())
        .setSpringContext(springContext)
        .buildInitializingBeanRunner()
        .afterPropertiesSet();

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
    // given
    when(changeEntryService.getExecuted()).thenReturn(Collections.emptyList());

    // when
    MongockSpringboot.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(IntegrationProfiledChangerLog.class.getPackage().getName())
        .setSpringContext(springContext)
        .buildInitializingBeanRunner()
        .afterPropertiesSet();

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
    MongockSpringboot.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(EnsureDecoratorChangerLog.class.getPackage().getName())
        .setSpringContext(springContext)
        .buildInitializingBeanRunner()
        .afterPropertiesSet();

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
        .buildInitializingBeanRunner()
        .afterPropertiesSet();
  }

}
