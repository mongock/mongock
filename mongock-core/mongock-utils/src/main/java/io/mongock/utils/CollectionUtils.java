package io.mongock.utils;

import java.util.Collection;

public final class CollectionUtils {

  private CollectionUtils() {
  }

  public static boolean isNullEmpty(Collection collection) {
    return collection == null || collection.isEmpty();
  }

  public static boolean isNotNullOrEmpty(Collection collection) {
    return !isNullEmpty(collection);
  }

  public static <T> boolean isNullEmpty(T[] collection) {
    return collection == null || collection.length <= 0;
  }

  public static <T> boolean isNotNullOrEmpty(T[] collection) {
    return !isNullEmpty(collection);
  }
}
