package io.mongock.utils;

import java.util.stream.Stream;

//TODO move to util module
public final class StringUtils {

  private StringUtils() {
  }

  public static boolean hasText(String str) {
    return str != null && !str.trim().isEmpty();
  }

  public static String getSimpleClassName(String changeLogClass) {
    String[] splitIn = changeLogClass.split("\\.");
    return splitIn[splitIn.length - 1];
  }

  public static String getStackTrace(Throwable th) {
    StackTraceElement[] ste = th.getStackTrace();
    StringBuilder sb = new StringBuilder(th.getClass().getName()).append(": ").append(th.getMessage()).append("\n");
    Stream.of(ste).forEach(e -> sb.append(e).append("\n"));
    return sb.toString();
  }
}
