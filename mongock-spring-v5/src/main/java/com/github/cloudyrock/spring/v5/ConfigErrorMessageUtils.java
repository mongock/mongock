package com.github.cloudyrock.spring.v5;

final class ConfigErrorMessageUtils {

  private final static String DRIVER_NOT_FOUND_ERROR = "MONGOCK DRIVER HAS NOT BEEN IMPORTED" +
      "\n====================================" +
      "\n\tSOLUTION: You need to import one of these artifacts" +
      "\n\t- 'mongodb-springdata-v3-driver' for springdata 3" +
      "\n\t- 'mongodb-springdata-v2-driver' for springdata 2";

  private ConfigErrorMessageUtils() {
  }

  public static String getDriverNotFoundErrorMessage() {
    return DRIVER_NOT_FOUND_ERROR;
  }


}
