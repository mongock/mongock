package com.github.cloudyrock.mongock;

public class StringUtils {

	  public static boolean hasText(String str) {
		    if (str == null || str.isEmpty() || str.isBlank()) {
		      return false;
		    }

		    return true;
		  }
}
