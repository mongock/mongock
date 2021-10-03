package io.mongock.runner.core.dependency;


import io.mongock.driver.api.driver.ChangeSetDependency;
import io.mongock.driver.api.lock.LockManager;
import io.mongock.driver.api.lock.guard.proxy.LockGuardProxyFactory;
import io.mongock.runner.core.executor.dependency.DependencyManager;
import io.mongock.runner.core.util.InterfaceDependency;
import io.mongock.runner.core.util.InterfaceDependencyImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class DependencyManagerTest {


  @Rule
  public ExpectedException exceptionExpected = ExpectedException.none();

  @Test
  public void shouldRetrieveConnectorDependency_WhenAddSimpleDependency() {
    assertEquals("dependency",
        new DependencyManager()
            .addDriverDependency(new ChangeSetDependency("dependency"))
            .getDependency(String.class, true)
            .orElseThrow(RuntimeException::new));
  }

  @Test
  public void shouldRetrieveLastConnectorDependency_WhenOverride() {
    assertEquals("dependency",
        new DependencyManager()
            .addDriverDependency(new ChangeSetDependency("dependencyNotReturned"))
            .addDriverDependency(new ChangeSetDependency("dependency"))
            .getDependency(String.class, true)
            .orElseThrow(RuntimeException::new));
  }

  @Test
  public void shouldRetrieveLastConnectorDependency_WhenAddSet() {
    ArrayList<ChangeSetDependency> dependencies = new ArrayList();
    dependencies.add(new ChangeSetDependency(100L));
    dependencies.add(new ChangeSetDependency("dependency"));
    assertEquals("dependency",
        new DependencyManager()
            .addDriverDependencies(dependencies)
            .getDependency(String.class, true)
            .orElseThrow(RuntimeException::new));
  }

  @Test
  public void shouldRetrieveLastConnectorDependency_WhenAddSetAndSimple_IfOverride() {
    ArrayList<ChangeSetDependency> dependencies = new ArrayList();
    dependencies.add(new ChangeSetDependency(100L));
    dependencies.add(new ChangeSetDependency("dependencyNotReturned"));
    assertEquals("dependency",
        new DependencyManager()
            .addDriverDependencies(dependencies)
            .addDriverDependency(new ChangeSetDependency("dependency"))
            .getDependency(String.class, true)
            .orElseThrow(RuntimeException::new));
  }


  @Test
  public void shouldRetrieveChildConnectorDependency_WhenAddChild_IfRetrievedParent() {

    InterfaceDependencyImpl dependency = new InterfaceDependencyImpl();

    assertEquals(dependency,
        new DependencyManager()
            .addDriverDependency(new ChangeSetDependency(InterfaceDependencyImpl.class, dependency))
            .getDependency(InterfaceDependency.class, true)
            .orElseThrow(RuntimeException::new));
  }

  @Test
  public void shouldRetrieveFirstChildConnectorDependency_WhenAddTwoChild_IfRetrievedParent() {
    Child1 dependency = new Child1();
    assertEquals(dependency,
        new DependencyManager()
            .addDriverDependency(new ChangeSetDependency(Child1.class, dependency))
            .addDriverDependency(new ChangeSetDependency(Child2.class, new Child2()))
            .getDependency(Parent.class, true)
            .orElseThrow(RuntimeException::new));
  }


  @Test
  public void shouldRetrieveStandardDependency_WhenAddSimpleDependency() {
    InterfaceDependency o = (InterfaceDependency) new DependencyManager()
        .setLockGuardProxyFactory(new LockGuardProxyFactory(Mockito.mock(LockManager.class)))
        .addStandardDependency(new ChangeSetDependency(new InterfaceDependencyImpl()))
        .getDependency(InterfaceDependency.class, true)
        .orElseThrow(RuntimeException::new);
  }

  @Test
  public void shouldRetrieveLastStandardDependency_WhenOverride() {
    assertEquals("value2",
        ((InterfaceDependency) new DependencyManager()
            .setLockGuardProxyFactory(new LockGuardProxyFactory(Mockito.mock(LockManager.class)))
            .addStandardDependency(new ChangeSetDependency(new Child2("value1")))
            .addStandardDependency(new ChangeSetDependency(new Child2("value2")))
            .getDependency(InterfaceDependency.class, true)
            .orElseThrow(RuntimeException::new)
        ).getValue());
  }

  @Test
  public void shouldRetrieveLastStandardDependency_WhenAddList() {
    ArrayList<ChangeSetDependency> dependencies = new ArrayList();
    dependencies.add(new ChangeSetDependency(new Child2("value1")));
    dependencies.add(new ChangeSetDependency(new Child2("value2")));
    assertEquals("value2",
        ((InterfaceDependency) new DependencyManager()
            .setLockGuardProxyFactory(new LockGuardProxyFactory(Mockito.mock(LockManager.class)))
            .addStandardDependencies(dependencies)
            .getDependency(InterfaceDependency.class, true)
            .orElseThrow(RuntimeException::new)
        ).getValue());
  }

  @Test
  public void shouldRetrieveLastStandardDependency_WhenAddSetAndSimple_IfOverride() {
    ArrayList<ChangeSetDependency> dependencies = new ArrayList<>();
    dependencies.add(new ChangeSetDependency(new Child2("value1")));
    dependencies.add(new ChangeSetDependency(new Child2("value2")));
    assertEquals("value3",
        ((InterfaceDependency) new DependencyManager()
            .setLockGuardProxyFactory(new LockGuardProxyFactory(Mockito.mock(LockManager.class)))
            .addStandardDependencies(dependencies)
            .addStandardDependency(new ChangeSetDependency(new Child2("value3")))
            .getDependency(InterfaceDependency.class, true)
            .orElseThrow(RuntimeException::new)
        ).getValue());
  }


  @Test
  public void shouldRetrieveFirstChildStandardDependency_WhenAddTwoDifferentChildrenOfTheSameTime_IfRetrievedParent() {

    assertEquals("dependency1",
        ((InterfaceDependency) new DependencyManager()
            .setLockGuardProxyFactory(new LockGuardProxyFactory(Mockito.mock(LockManager.class)))
            .addStandardDependency(new ChangeSetDependency(Child1.class, new Child1("dependency1")))
            .addStandardDependency(new ChangeSetDependency(Child2.class, new Child2("dependency12")))
            .getDependency(InterfaceDependency.class, true)
            .orElseThrow(RuntimeException::new)
        ).getValue());
  }


  @Test
  public void shouldPrioritizeConnectorDependency() {
    assertEquals("connectorDependency",
        new DependencyManager()
            .addStandardDependency(new ChangeSetDependency("standardDependency"))
            .addDriverDependency(new ChangeSetDependency("connectorDependency"))
            .addStandardDependency(new ChangeSetDependency("standardDependency"))
            .getDependency(String.class, true)
            .orElseThrow(RuntimeException::new));
  }
}

class Parent implements InterfaceDependency {
}

class Child1 extends Parent {

  private final String value;

  public Child1() {
    this("child1");
  }

  public Child1(String value) {
    this.value = value;
  }

  @Override
  public String getValue() {
    return value;
  }

}

class Child2 extends Parent {

  private final String value;

  public Child2() {
    this("defaultValue");
  }

  public Child2(String value) {
    this.value = value;
  }

  @Override
  public String getValue() {
    return value;
  }
}

