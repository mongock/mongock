package io.mongock.utils;

import java.net.ContentHandlerFactory;
import java.util.Map;

public final class Utils {

  private Utils() {
  }

  public static boolean isBasicTypeJDK(Class<?> clazz) {
    return clazz.isPrimitive()
        || String.class.equals(clazz)
        || Class.class.equals(clazz)
        || isJDKWrapper(clazz)
        || isJDKDataStructure(clazz)
        || isOtherWellKnownClassesNonProxiable(clazz);


  }

  //should be added all the extra classes that shouldn't be proxiable
  private static boolean isOtherWellKnownClassesNonProxiable(Class<?> clazz) {
    return ContentHandlerFactory.class.isAssignableFrom(clazz);
  }

  private static boolean isJDKWrapper(Class<?> clazz) {
    return Boolean.class.equals(clazz)
        || Character.class.equals(clazz)
        || Byte.class.equals(clazz)
        || Short.class.equals(clazz)
        || Integer.class.equals(clazz)
        || Long.class.equals(clazz)
        || Float.class.equals(clazz)
        || Double.class.equals(clazz)
        || Void.class.equals(clazz);
  }

  private static boolean isJDKDataStructure(Class<?> clazz) {
    return Iterable.class.isAssignableFrom(clazz)
        || Map.class.isAssignableFrom(clazz);
    //should be added all the JDK data structure that shouldn't be proxied
  }
}
