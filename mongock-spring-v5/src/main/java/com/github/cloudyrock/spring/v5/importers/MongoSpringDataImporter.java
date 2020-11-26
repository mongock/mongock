package com.github.cloudyrock.spring.v5.importers;

public class MongoSpringDataImporter implements ContextImporter {
  @Override
  public String[] getPaths() {
    try {
      return loadSpringDataContextV3();
    } catch (ClassNotFoundException e) {
      try {
        return loadSpringDataContextV2();
      } catch (ClassNotFoundException e2) {
        return null;
      }
    }
  }

  private String[] loadSpringDataContextV2() throws ClassNotFoundException {
    Class.forName("com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.SpringDataMongo2Driver");
    return new String[]{
        "com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.MongockSpringDataV3Configuration",
        "com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.MongockSpringDataV2Context"
    };
  }

  private String[] loadSpringDataContextV3() throws ClassNotFoundException {
    Class.forName("com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.SpringDataMongo3Driver");
    return new String[]{
        "com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.MongockSpringDataV3Configuration",
        "com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.MongockSpringDataV3Context"
    };
  }
}
