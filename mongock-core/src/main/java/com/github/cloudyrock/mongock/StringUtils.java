package com.github.cloudyrock.mongock;

public class StringUtils {

  public static boolean hasText(String str) {
    if (str == null || str.isEmpty()) {
      return false;
    }
    int strLen = str.length();
    for (int i = 0; i < strLen; i++) {
      if (!Character.isWhitespace(str.charAt(i))) {
        return true;
      }
    }
    return false;
  }
}
