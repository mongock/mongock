package io.mongock.driver.api.lock.guard.proxy;

import io.mongock.driver.api.lock.LockManager;
import java.lang.reflect.InvocationTargetException;
import javassist.util.proxy.MethodHandler;

import java.lang.reflect.Method;
import java.util.Set;

public class LockGuardMethodHandler<T> implements MethodHandler {

  private final LockGuardProxy<T> lockGuardProxy;

  public LockGuardMethodHandler(T implementation, LockManager lockManager, LockGuardProxyFactory proxyFactory, Set<String> nonGuardedMethods) {
    this.lockGuardProxy = new LockGuardProxy<>(implementation, lockManager, proxyFactory, nonGuardedMethods);
  }

  public LockGuardProxy<T> getLockGuardProxy() {
    return lockGuardProxy;
  }

  @Override
  public Object invoke(Object proxy, Method method, Method method1, Object[] methodArgs) throws Throwable {
    try {
      return lockGuardProxy.invoke(proxy, method, methodArgs);
    } catch (InvocationTargetException ex) {
      if (ex.getTargetException() != null) {
        throw ex.getTargetException();
      }
      throw ex;
    }
  }
}
