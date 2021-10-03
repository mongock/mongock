package io.mongock.util.test;

import javassist.util.proxy.ProxyFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

// Doing nasty things to avoid change production code
public final class ReflectionUtils {

  private ReflectionUtils() {}

  public static Object getImplementationFromLockGuardProxy(Object proxiedObject) {
    try {
      Object lockGuardHandler = getLockGuardProxyHandler(proxiedObject);
      Object lockGuardProxy = ReflectionUtils.getFinalFieldFromObject(lockGuardHandler, "lockGuardProxy");
      return ReflectionUtils.getFinalFieldFromObject(lockGuardProxy, "implementation");

    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static Object getFinalFieldFromObject(Object object, String fieldName) {
    try {
      Field field = object.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      Field modifiersField = Field.class.getDeclaredField("modifiers");
      modifiersField.setAccessible(true);
      modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
      return field.get(object);
    } catch(Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static boolean isProxy(Object object) {
    return Proxy.isProxyClass(object.getClass()) || ProxyFactory.isProxyClass(object.getClass());
  }

  private static Object getLockGuardProxyHandler(Object object) {
    try {
      return getPrivateField(object, object.getClass(), "h");
    } catch (Exception ex) {
      return getPrivateField(object, object.getClass(), "handler");
    }
  }

  public static Object getPrivateField(Object object, Class<?> clazz, String fieldName) {

    if(clazz == null) {
      throw new RuntimeException("field not found");
    }
    try {
      Field field = clazz.getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(object);
    } catch (NoSuchFieldException e) {
      if(Object.class.equals(object.getClass())) {
        throw new RuntimeException(e);
      }
      return getPrivateField(object, clazz.getSuperclass(), fieldName);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }

  }
}
