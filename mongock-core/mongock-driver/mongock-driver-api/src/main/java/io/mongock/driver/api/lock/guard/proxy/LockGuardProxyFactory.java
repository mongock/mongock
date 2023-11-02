package io.mongock.driver.api.lock.guard.proxy;

import io.changock.migration.api.annotations.NonLockGuarded;
import io.mongock.driver.api.lock.LockManager;
import io.mongock.utils.Constants;
import io.mongock.utils.JdkUtil;
import javassist.util.proxy.ProxyFactory;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LockGuardProxyFactory {

  private static final Set<String> DEFAULT_NON_GUARDED_METHODS = new HashSet<>(
      Collections.singletonList("finalize")
  );

  static {
    ProxyFactory.nameGenerator = new ProxyFactory.UniqueName() {
      private final String sep = Constants.PROXY_MONGOCK_PREFIX + Integer.toHexString(this.hashCode() & 0xfff) + "_";
      private int counter = 0;

      @Override
      public String get(String classname) {
        return classname + sep + Integer.toHexString(counter++);
      }
    };
  }

  private final LockManager lockManager;
  private final Collection<String> notProxiedPackagePrefixes;
  private final Set<String> nonGuardedMethods;
  private final boolean lockGuardEnabled;

  public LockGuardProxyFactory(LockManager lockManager, boolean lockGuardEnabled) {
    this(lockManager, Collections.emptyList(), DEFAULT_NON_GUARDED_METHODS, lockGuardEnabled);
  }

  public LockGuardProxyFactory(LockManager lockManager, Collection<String> notProxiedPackagePrefixes, boolean lockGuardEnabled) {
    this(lockManager, notProxiedPackagePrefixes, DEFAULT_NON_GUARDED_METHODS, lockGuardEnabled);
  }

  public LockGuardProxyFactory(LockManager lockManager, Collection<String> notProxiedPackagePrefixes, Set<String> nonGuardedMethods, boolean lockGuardEnabled) {
    this.lockManager = lockManager;
    this.notProxiedPackagePrefixes = notProxiedPackagePrefixes;
    this.nonGuardedMethods = nonGuardedMethods;
    this.lockGuardEnabled = lockGuardEnabled;
  }

  @SuppressWarnings("unchecked")
  public <T> T getProxy(T targetObject, Class<? super T> interfaceType) {
    return (T) getRawProxy(targetObject, interfaceType);
  }

  @SuppressWarnings("unchecked")
  public Object getRawProxy(Object targetObject, Class<?> interfaceType) {
    return shouldBeLockGuardProxied(targetObject, interfaceType) ? createProxy(targetObject, interfaceType) : targetObject;
  }

  //TODO add here check
  private boolean shouldBeLockGuardProxied(Object targetObject, Class<?> interfaceType) {

    return targetObject != null
        && lockGuardEnabled
        && !Modifier.isFinal(interfaceType.getModifiers())
        && isPackageProxiable(interfaceType.getPackage().getName())
        && !interfaceType.isAnnotationPresent(NonLockGuarded.class)
        && !targetObject.getClass().isAnnotationPresent(NonLockGuarded.class)
        && !JdkUtil.isInternalJdkClass(targetObject.getClass())
        && !JdkUtil.isInternalJdkClass(interfaceType);
  }

  private boolean isPackageProxiable(String packageName) {
    return notProxiedPackagePrefixes.stream().noneMatch(packageName::startsWith);
  }

  private Object createProxy(Object impl, Class<?> type) {

    ProxyFactory proxyFactory = new ProxyFactory();
    if (type.isInterface()) {
      proxyFactory.setInterfaces(new Class<?>[]{type});
    } else {
      proxyFactory.setSuperclass(type);
    }

    Object proxyInstance = new ObjenesisStd()
        .getInstantiatorOf(proxyFactory.createClass())
        .newInstance();

    ((javassist.util.proxy.Proxy) proxyInstance).setHandler(new LockGuardMethodHandler<>(impl, lockManager, this, nonGuardedMethods));
    return proxyInstance;
  }

  public static boolean isProxy(Object obj) {
    return isProxyClass(obj.getClass());
  }

  public static boolean isProxyClass(Class<?> c) {
    return Proxy.isProxyClass(c) || ProxyFactory.isProxyClass(c);
  }

  public static void checkProxy(Object obj) {
    if(!isProxyClass(obj.getClass())) {
      throw new RuntimeException("Is not proxy");
    }
  }

}
