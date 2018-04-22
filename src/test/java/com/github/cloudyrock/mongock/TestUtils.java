package com.github.cloudyrock.mongock;

import java.lang.reflect.Field;

class TestUtils {

  static void setField(Object object, String fieldName, Object jongInstance) {
    Field f = null;
    Class clazz = object.getClass();
    boolean loop = true;
    while (clazz != null && loop) {
      boolean accessible = false;
      try {
        f = clazz.getDeclaredField(fieldName);
        accessible = f.isAccessible();
        f.setAccessible(true);
        f.set(object, jongInstance);
        loop = false;
      } catch (NoSuchFieldException | IllegalAccessException e) {
        clazz = clazz.getSuperclass();
      } finally {
        if (f != null) {
          f.setAccessible(accessible);
        }
      }
    }

  }
}
