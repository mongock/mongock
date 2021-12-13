package io.mongock.integrationtests.spring5.springdata3;

import io.mongock.integrationtests.spring5.springdata3.util.MongoContainer;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class RuntimeTestUtil {

  public static final String DEFAULT_DATABASE_NAME = "mongocktest";


  public static MongoContainer startMongoDbContainer(String mongoDockerImage) {
    MongoContainer mongoContiner = MongoContainer.builder().mongoDockerImageName(mongoDockerImage).build();
    mongoContiner.start();

    String replicaSetUrl = mongoContiner.getReplicaSetUrl();
    return mongoContiner;
  }

  public static ConfigurableApplicationContext startSpringAppWithMongoDbVersionAndDefaultPackage(String mongoVersion) {
    return startSpringAppWithMongoDbVersionAndDefaultPackage(
        mongoVersion,
        false
    );
  }

  public static ConfigurableApplicationContext startSpringAppWithMongoDbVersionAndDefaultPackage(String mongoVersion, boolean sleuthEnabled) {
    return startSpringAppWithMongoDbVersionAndPackage(
        mongoVersion,
        "io.mongock.integrationtests.spring5.springdata3.changelogs.client",
        sleuthEnabled
    );
  }

  public static ConfigurableApplicationContext startSpringAppWithMongoDbVersionAndNoPackage(String mongoDBVersion, boolean sleuthEnabled) {
    Map<String, String> parameters = new HashMap<>();
    parameters.put("spring.sleuth.enabled", String.valueOf(sleuthEnabled));
    parameters.put("mongock.changeLogsScanPackage", "");
    parameters.put("mongock.transactionable", "false");
    return startSpringAppWithMongoDbVersionAndParameters(mongoDBVersion, parameters);
  }

  public static ConfigurableApplicationContext startSpringAppWithMongoDbVersionAndNoPackage(String mongoDBVersion) {
    return startSpringAppWithMongoDbVersionAndNoPackage(mongoDBVersion, false);
  }

  public static ConfigurableApplicationContext startSpringAppWithMongoDbVersionAndPackage(String mongoDBVersion, String packagePath) {
    return startSpringAppWithMongoDbVersionAndPackage(mongoDBVersion, packagePath, false);
  }

  public static ConfigurableApplicationContext startSpringAppWithMongoDbVersionAndPackage(String mongoDBVersion, String packagePath, boolean sleuthEnabled) {
    Map<String, String> parameters = new HashMap<>();
    parameters.put("spring.sleuth.enabled", String.valueOf(sleuthEnabled));
    parameters.put("mongock.changeLogsScanPackage", packagePath);
    return startSpringAppWithMongoDbVersionAndParameters(mongoDBVersion, parameters);
  }

  public static ConfigurableApplicationContext startSpringAppWithTransactionDisabledMongoDbVersionAndPackage(String mongoDBVersion, String packagePath, boolean sleuthEnabled) {
    Map<String, String> parameters = new HashMap<>();
    parameters.put("spring.sleuth.enabled", String.valueOf(sleuthEnabled));
    parameters.put("mongock.changeLogsScanPackage", packagePath);
    parameters.put("mongock.transactionable", "false");
    return startSpringAppWithMongoDbVersionAndParameters(mongoDBVersion, parameters);
  }

  public static ConfigurableApplicationContext startSpringAppWithTransactionDisabledMongoDbVersionAndPackage(String mongoDBVersion, String packagePath) {
    return startSpringAppWithTransactionDisabledMongoDbVersionAndPackage(mongoDBVersion, packagePath, false);
  }

  public static ConfigurableApplicationContext startSpringAppWithMongoDbVersionAndParameters(String mongoDBVersion, Map<String, String> parameters) {
    return startSpringAppWithParameters(startMongoDbContainer(mongoDBVersion), parameters);
  }


  public static ConfigurableApplicationContext startSpringAppWithParameters(MongoContainer container, Map<String, String> parameters) {
    String[] parametersArray = getParametersArray(container, parameters);
    return Mongock4Spring5SpringData3App.getSpringAppBuilder().properties(parametersArray).run();
  }

  private static String[] getParametersArray(MongoContainer container, Map<String, String> parameters) {
    String replicaSetUrl = container.getReplicaSetUrl();
    List<String> parametersList = parameters.entrySet()
        .stream()
        .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
    if (!parameters.containsKey("server.port")) {
      parametersList.add("server.port=0");
    }
    if (!parameters.containsKey("mongock.transactionable")) {
      parametersList.add("mongock.transactionable=" + container.isTransactionable());
    }
    parametersList.add("spring.data.mongodb.uri=" + replicaSetUrl);
    parametersList.add("spring.data.mongodb.database=" + DEFAULT_DATABASE_NAME);
    String[] parametersArray = new String[parametersList.size()];
    return parametersList.toArray(parametersArray);
  }


}
