package io.mongock.utils;

import java.net.ContentHandlerFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class JdkUtil {

  private JdkUtil() {
  }

  private static final List<String> jdkInternalPackages = Arrays.asList("java.", "com.sun.", "javax.", "jdk.", "sun.");

  public static boolean isInternalJdkClass(Class<?> clazz) {
    return clazz.isPrimitive()
        || isJdkNativeType(clazz)
        || isJdkDataStructure(clazz)
        || isInternalJdkPackage(clazz)
        || isOtherWellKnownClassesNonProxiable(clazz);
  }

  private static boolean isInternalJdkPackage(Class<?> clazz) {
    String packageName = clazz.getPackage().getName();
    return jdkInternalPackages.stream().anyMatch(packageName::startsWith);
  }

  //should be added all the extra classes that shouldn't be proxiable
  private static boolean isOtherWellKnownClassesNonProxiable(Class<?> clazz) {
    return ContentHandlerFactory.class.isAssignableFrom(clazz);
  }

  private static boolean isJdkNativeType(Class<?> clazz) {
    return Boolean.class.equals(clazz)
        || String.class.equals(clazz)
        || Class.class.equals(clazz)
        || Character.class.equals(clazz)
        || Byte.class.equals(clazz)
        || Short.class.equals(clazz)
        || Integer.class.equals(clazz)
        || Long.class.equals(clazz)
        || Float.class.equals(clazz)
        || Double.class.equals(clazz)
        || Void.class.equals(clazz);
  }

  private static boolean isJdkDataStructure(Class<?> clazz) {
    return Iterable.class.isAssignableFrom(clazz)
        || Map.class.isAssignableFrom(clazz);
    //should be added all the JDK data structure that shouldn't be proxied
  }
}
