package io.mongock.driver.api.lock.guard.proxy;

import io.mongock.driver.api.lock.LockManager;
import io.mongock.driver.api.lock.guard.proxy.util.InterfaceType;
import io.mongock.driver.api.lock.guard.proxy.util.InterfaceTypeImpl;
import io.mongock.util.test.ReflectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.verification.Times;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class LockGuardProxyTest {

  private LockManager lockManager;
  private InterfaceType proxy;

  @Before
  public void before() {
    lockManager = mock(LockManager.class);
    proxy = new LockGuardProxyFactory(lockManager).getProxy(new InterfaceTypeImpl(), InterfaceType.class);
  }

  //SHOULD RETURN PROXY

  @Test
  public void shouldNotCallEnsureLock_WhenCallingMethod_IfFinalize() {

    proxy = new LockGuardProxyFactory(
        lockManager,
        Collections.emptyList(),
        new HashSet<>(Collections.singletonList("fakeFinalize")))
        .getProxy(new InterfaceTypeImpl(), InterfaceType.class);
    proxy.fakeFinalize();
    verify(lockManager, new Times(0)).ensureLockDefault();
  }

  @Test
  public void shouldReturnProxy() {
    assertTrue(ReflectionUtils.isProxy(proxy.getGuardedImpl()));
  }

  @Test
  public void shouldReturnProxy_IfMethodAnnotatedWithNonLockGuardDefault() {
    assertTrue(ReflectionUtils.isProxy(proxy.getGuardedImplWithAnnotationDefault()));
  }

  @Test
  public void shouldReturnProxy_IfMethodAnnotatedWithNonLockGuardMethod() {
    assertTrue(ReflectionUtils.isProxy(proxy.getGuardedImplWithAnnotationMethod()));
  }

  @Test
  public void shouldReturnProxy_IfReturningClassIsNotAnInterface() {
    assertTrue(ReflectionUtils.isProxy(proxy.getNontInterfacedClass()));
  }

  // SHOULD NOT RETURN PROXY
  @Test
  public void shouldNotReturnProxy_IfReturningIsString() {
    assertFalse(ReflectionUtils.isProxy(proxy.getString()));
  }

  @Test
  public void shouldNotReturnProxy_IfReturningIsPrimitive() {
    assertFalse(ReflectionUtils.isProxy(proxy.getPrimitive()));
  }

  @Test
  public void shouldNotReturnProxy_IfReturningIsPrimitiveWrapper() {
    assertFalse(ReflectionUtils.isProxy(proxy.getPrimitiveWrapper()));
  }

  @Test
  public void shouldNotReturnProxy_IfReturningIsClassType() {
    assertFalse(ReflectionUtils.isProxy(proxy.getClassType()));
  }

  @Test
  public void shouldNotReturnProxy_IfReturningIsAnnotated() {
    assertFalse(ReflectionUtils.isProxy(proxy.getNonGuardedImpl()));
  }

  @Test
  public void shouldNotReturnProxy_WhenMethodNonLockGuardMethod_IfReturningIsAnnotated() {
    assertFalse(ReflectionUtils.isProxy(proxy.getNonGuardedImplWithAnnotationMethod()));
  }

  @Test
  public void shouldNotReturnProxy_WhenMethodNonLockGuardNone() {
    assertFalse(ReflectionUtils.isProxy(proxy.getGuardedImplWithAnnotationNone()));
  }

  // SHOULD BE LOCK GUARDED

  @Test
  public void shouldBeLockGuarded() {
    proxy.getGuardedImpl();
    verify(lockManager, new Times(1)).ensureLockDefault();
  }

  @Test
  public void shouldBeLockGuarded_ifVoidMethod() {
    proxy.voidMethod();
    verify(lockManager, new Times(1)).ensureLockDefault();
  }

  // SHOULD NOT BE LOCK-GUARDED

  @Test
  public void shouldNotBeLockGuarded_IfAnnotated() {
    proxy.callMethodNoLockGuarded();
    verify(lockManager, new Times(0)).ensureLockDefault();
  }

  @Test
  public void shouldNotBeLockGuarded_IfMethodAnnotatedWithNonLockGuardDefault() {
    proxy.getGuardedImplWithAnnotationDefault();
    verify(lockManager, new Times(0)).ensureLockDefault();
  }


  @Test
  public void shouldNotBeLockGuarded_IfMethodAnnotatedWithNonLockGuardMethod() {
    proxy.getGuardedImplWithAnnotationMethod();
    verify(lockManager, new Times(0)).ensureLockDefault();
  }

  @Test
  public void shouldNotBeLockGuarded_WhenMethodNonLockGuardNone() {
    proxy.getGuardedImplWithAnnotationNone();
    verify(lockManager, new Times(0)).ensureLockDefault();
  }

  @Test
  public void shouldNotBeLockGuarded_WhenMethodNonLockGuardMethod_IfReturningIsAnnotated() {
    proxy.getNonGuardedImplWithAnnotationMethod();
    verify(lockManager, new Times(0)).ensureLockDefault();
  }

}
