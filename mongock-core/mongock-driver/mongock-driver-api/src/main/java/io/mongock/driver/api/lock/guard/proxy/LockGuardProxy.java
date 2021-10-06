package io.mongock.driver.api.lock.guard.proxy;

import io.changock.migration.api.annotations.NonLockGuarded;
import io.changock.migration.api.annotations.NonLockGuardedType;
import io.mongock.driver.api.lock.LockManager;
import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//TODO add tests
public class LockGuardProxy<T> implements InvocationHandler {

  private final LockManager lockManager;
  private final T implementation;
  private final LockGuardProxyFactory proxyFactory;
  private final Set<String> nonGuardedMethods;

  public LockGuardProxy(T implementation, LockManager lockManager, LockGuardProxyFactory proxyFactory, Set<String> nonGuardedMethods) {
    this.implementation = implementation;
    this.lockManager = lockManager;
    this.proxyFactory = proxyFactory;
    this.nonGuardedMethods = nonGuardedMethods;
  }


  private static boolean shouldTryProxyReturn(List<NonLockGuardedType> methodNoGuardedLockTypes, Type type) {
    return
        !(type instanceof TypeVariableImpl && ((TypeVariableImpl) type).getGenericDeclaration() != null )
        && !methodNoGuardedLockTypes.contains(NonLockGuardedType.RETURN)
        && !methodNoGuardedLockTypes.contains(NonLockGuardedType.NONE);
  }

  private  boolean shouldMethodBeLockGuarded(Method method, List<NonLockGuardedType> noGuardedLockTypes) {
    String temporalVariable = method.getName();
    return !nonGuardedMethods.contains(method.getName())
        && !noGuardedLockTypes.contains(NonLockGuardedType.METHOD)
        && !noGuardedLockTypes.contains(NonLockGuardedType.NONE);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    NonLockGuarded nonLockGuarded = method.getAnnotation(NonLockGuarded.class);
    List<NonLockGuardedType> noGuardedLockTypes = nonLockGuarded != null ? Arrays.asList(nonLockGuarded.value()) : Collections.emptyList();
    if (shouldMethodBeLockGuarded(method, noGuardedLockTypes)) {
      lockManager.ensureLockDefault();
    }

    return shouldTryProxyReturn(noGuardedLockTypes, method.getGenericReturnType())
        ? proxyFactory.getRawProxy(method.invoke(implementation, args), method.getReturnType())
        : method.invoke(implementation, args);
  }


}
