package io.mongock.utils;

public final class Constants {

  public static final String PROXY_MONGOCK_PREFIX = "_$$_mongock_";
  public static final String CLI_PROFILE = "mongock-cli-profile";
  public static final String NON_CLI_PROFILE = "!" + CLI_PROFILE;


  public static long DEFAULT_LOCK_ACQUIRED_FOR_MILLIS = 60 * 1000L;//1 minute
  public static long DEFAULT_QUIT_TRYING_AFTER_MILLIS = 3 * 60 * 1000L;//3 minutes
  public static long DEFAULT_TRY_FREQUENCY_MILLIS = 1000L;//1 second

  private Constants() {}



}
