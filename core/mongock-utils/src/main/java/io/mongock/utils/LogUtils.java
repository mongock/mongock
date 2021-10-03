package io.mongock.utils;

import org.slf4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

//TODO move to util module
public final class LogUtils {

  private LogUtils() {
  }

  public static void logMethodWithArguments(Logger logger, String methodName, List<Object> changelogInvocationParameters) {
    String arguments = changelogInvocationParameters.stream()
        .map(LogUtils::getParameterType)
        .collect(Collectors.joining(", "));
    logger.info("method[{}] with arguments: [{}]", methodName, arguments);

  }

  private static String getParameterType(Object obj) {
    String className = obj != null ? obj.getClass().getName() : "{null argument}";
    int mongockProxyPrefixIndex = className.indexOf(Constants.PROXY_MONGOCK_PREFIX);
    if(mongockProxyPrefixIndex > 0) {
      return className.substring(0, mongockProxyPrefixIndex);
    } else {
      return className;
    }
  }
}
