package com.github.cloudyrock.spring.v5;

import io.changock.migration.api.exception.ChangockException;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class MongockCoreContextSelector implements ImportSelector {


  private final static String DRIVER_NOT_FOUND_ERROR = "MONGOCK DRIVER HAS NOT BEEN IMPORTED" +
      "\n====================================" +
      "\n\tSOLUTION: You need to import one of these artifacts" +
      "\n\t- 'mongodb-springdata-v3-driver' for springdata 3" +
      "\n\t- 'mongodb-springdata-v2-driver' for springdata 2";




  @Override
  public String[] selectImports(AnnotationMetadata importingClassMetadata) {
    try {
      return loadSpringDataContextV3();
    } catch (ClassNotFoundException e) {
      try {
        return loadSpringDataContextV2();
      } catch (ClassNotFoundException e2) {
        throw new ChangockException(String.format("\n\n%s\n\n", DRIVER_NOT_FOUND_ERROR));

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
