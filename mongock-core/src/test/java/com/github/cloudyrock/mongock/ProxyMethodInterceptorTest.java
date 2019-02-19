package com.github.cloudyrock.mongock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.verification.Times;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @since 04/04/2018
 */
public class ProxyMethodInterceptorTest {

  private PreInterceptor lockCheckerInterceptorMock;

  @Before
  public void setUp() {
    lockCheckerInterceptorMock = mock(PreInterceptor.class);
  }

  @Test
  public void shouldCallChecker() throws Throwable {
    final DummyClass dummyInstance = new DummyClass("value1");
    ProxyMethodInterceptor interceptor = new ProxyMethodInterceptor(
        dummyInstance,
        null,
        lockCheckerInterceptorMock,
        null,
        null
    );
    interceptor.intercept(
        dummyInstance,
        DummyClass.class.getMethod("getValue"),
        new Object[0],
        null

    );
    verify(lockCheckerInterceptorMock, new Times(1)).before();
  }

  @Test
  public void shouldNotCallCheckerWhenUncheckedMethod() throws Throwable {
    final DummyClass dummyInstance = new DummyClass("value1");
    ProxyMethodInterceptor interceptor = new ProxyMethodInterceptor(
        dummyInstance,
        null,
        lockCheckerInterceptorMock,
        null,
        Collections.singleton("getValue")
    );
    interceptor.intercept(
        dummyInstance,
        DummyClass.class.getMethod("getValue"),
        new Object[0],
        null

    );
    verify(lockCheckerInterceptorMock, new Times(0)).before();
  }

  @Test
  public void shouldNotCallCheckerAndReturnAProxyWhenUncheckedMethodAndProxyCreator() throws Throwable {
    final DummyClass dummyInstance = new DummyClass("value1");

    ProxyFactory proxyFactory = mock(ProxyFactory.class);
    when(proxyFactory.createProxyFromOriginal(dummyInstance.getValue(), String.class)).thenReturn("ProxiedObject");
    ProxyMethodInterceptor interceptor = new ProxyMethodInterceptor(
        dummyInstance,
        proxyFactory,
        lockCheckerInterceptorMock,
        Collections.singleton("getValue"),
        Collections.singleton("getValue")
    );
    Object result = interceptor.intercept(
        dummyInstance,
        DummyClass.class.getMethod("getValue"),
        new Object[0],
        null

    );
    verify(lockCheckerInterceptorMock, new Times(0)).before();
    verify(proxyFactory, new Times(1)).createProxyFromOriginal(dummyInstance.getValue(), String.class);
    assertEquals("ProxiedObject", result);
  }

  @Test(expected = DummyClass.DummyException.class)
  public void shouldPropagateExceptionAsIs() throws Throwable {
    final DummyClass dummyInstance = new DummyClass("value1");

    ProxyFactory proxyFactory = mock(ProxyFactory.class);
    ProxyMethodInterceptor interceptor = new ProxyMethodInterceptor(
        dummyInstance,
        proxyFactory,
        lockCheckerInterceptorMock,
        Collections.singleton("throwException"),
        Collections.singleton("throwException")
    );

    interceptor.intercept(
        dummyInstance,
        DummyClass.class.getMethod("throwException"),
        new Object[0],
        null
    );
  }

}
