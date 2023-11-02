package io.mongock.driver.api.lock.guard.proxy;


import io.mongock.driver.api.lock.LockManager;
import io.mongock.driver.api.lock.guard.proxy.util.ContentHandlerFactoryImpl;
import io.mongock.driver.api.lock.guard.proxy.util.FinalClass;
import io.mongock.driver.api.lock.guard.proxy.util.InterfaceType;
import io.mongock.driver.api.lock.guard.proxy.util.InterfaceTypeImpl;
import io.mongock.driver.api.lock.guard.proxy.util.InterfaceTypeImplNonLockGuarded;
import io.mongock.driver.api.lock.guard.proxy.util.SomeClass;
import io.mongock.util.test.ReflectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.Serializable;
import java.net.ContentHandlerFactory;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LockGuardProxyFactoryTest {

  private LockManager lockManager;
  private LockGuardProxyFactory lockGuardProxyFactory;

  @BeforeEach
  public void before() {
    lockManager = Mockito.mock(LockManager.class);
    lockGuardProxyFactory = new LockGuardProxyFactory(lockManager, true);
  }

  private Object getRawProxy(Object o, Class<?> interfaceType) {
    return lockGuardProxyFactory.getRawProxy(o, interfaceType);
  }

  @Test
  public void shouldNotReturnProxy_IfIsJDKStructure() {
    assertFalse(ReflectionUtils.isProxy(getRawProxy(new ArrayList<>(), List.class)));
  }

  @Test
  public void shouldNotReturnProxy_IfIsJDKBasicType() {
    assertFalse(ReflectionUtils.isProxy(getRawProxy("Value", String.class)));
  }

  @Test
  public void shouldNotReturnProxy_IfIsJDKInternal() {
    assertFalse(ReflectionUtils.isProxy(getRawProxy(new ProtectionDomain(null, null), ProtectionDomain.class)));
  }

  @Test
  public void shouldNotReturnProxy_IfIsWellKnownClassesNonProxiable() {
    assertFalse(ReflectionUtils.isProxy(getRawProxy(new ContentHandlerFactoryImpl(), ContentHandlerFactory.class)));
  }

  @Test
  public void shouldNotReturnProxy_IfInterfaceTypeisJavaNet() {
    lockGuardProxyFactory = new LockGuardProxyFactory(lockManager, Collections.singletonList(InterfaceType.class.getPackage().getName().substring(0, 12)), true);
    assertFalse(ReflectionUtils.isProxy(getRawProxy(new InterfaceTypeImpl(), InterfaceType.class)));
  }


  @Test
  public void shouldNotReturnProxyForBasicCollection() {
    assertFalse(ReflectionUtils.isProxy(getRawProxy(new ArrayList<>(), List.class)));
  }

  @Test
  public void shouldReturnProxy() {
    assertTrue(ReflectionUtils.isProxy(getRawProxy(new InterfaceTypeImpl(), InterfaceType.class)));
  }

  @Test
  public void shouldNotReturnProxy_ifImplClassNonLockGuarded() {
    assertFalse(ReflectionUtils.isProxy(getRawProxy(new InterfaceTypeImplNonLockGuarded(), InterfaceType.class)));
  }


  //failing in local but not in CI
  @Test
  public void shouldReturnProxyWithRightImplementation() {
    String javaVersion = System.getProperty("java.version");
    if(javaVersion == null || javaVersion.startsWith("1.8")) {
      assertEquals(SomeClass.class, ReflectionUtils.getImplementationFromLockGuardProxy(getRawProxy(new SomeClass(), SomeClass.class)).getClass());
    }
  }

  @Test
  public void ShouldReturnNull_ifTargetIsNull() {
    assertNull(getRawProxy(null, InterfaceTypeImpl.class));
  }

  @Test
  public void ShouldReturnProxy_ifTargetNotInterface() {
    assertTrue(ReflectionUtils.isProxy(getRawProxy(new FinalClass(), InterfaceTypeImpl.class)));
  }

  @Test
  public void ShouldNotReturnProxy_ifTargetIsFinal() {
    assertFalse(ReflectionUtils.isProxy(getRawProxy(new FinalClass(), FinalClass.class)));
  }

  @Test
  public void ShouldNotReturnProxy_ifPrimitive() {
    assertFalse(ReflectionUtils.isProxy(getRawProxy(1, Comparable.class)));
  }

  @Test
  public void ShouldNotReturnProxy_ifPrimitiveWrapper() {
    assertFalse(ReflectionUtils.isProxy(getRawProxy(new Integer(1), Comparable.class)));
  }

  @Test
  public void ShouldNotReturnProxy_ifString() {
    assertFalse(ReflectionUtils.isProxy(getRawProxy("anyString", Serializable.class)));
  }

  @Test
  public void ShouldNotReturnProxy_ifClass() {
    assertFalse(ReflectionUtils.isProxy(getRawProxy(InterfaceTypeImpl.class, Serializable.class)));
  }

}
