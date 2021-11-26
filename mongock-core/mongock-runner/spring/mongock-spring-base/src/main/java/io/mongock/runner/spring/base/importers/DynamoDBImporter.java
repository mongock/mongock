package io.mongock.runner.spring.base.importers;

import java.util.Collections;
import java.util.List;

public class DynamoDBImporter implements ContextImporter {

  private static final String DYNAMODB_PACKAGE_PREFIX = "io.mongock.driver.dynamodb";

  @Override
  public String[] getPaths() {
    try {
      return loadDynamoDBDrive();
    } catch (ClassNotFoundException e) {
      return new String[]{};
    }
  }

  @Override
  public List<ArtifactDescriptor> getArtifacts() {
    return Collections.singletonList(new ArtifactDescriptor("DynamoDB", "io.mongock:dynamodb-driver"));
  }

  private String[] loadDynamoDBDrive() throws ClassNotFoundException {
    Class.forName(DYNAMODB_PACKAGE_PREFIX + ".driver.DynamoDBDriver");
    return new String[]{DYNAMODB_PACKAGE_PREFIX + ".springboot.config.DynamoDBSpringbootContext"};
  }


}
