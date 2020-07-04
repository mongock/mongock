package com.github.cloudyrock.spring.v5;

import io.changock.migration.api.exception.ChangockException;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class MongockCoreContextSelector implements ImportSelector {
  @Override
  public String[] selectImports(AnnotationMetadata importingClassMetadata) {
    try {
      Class.forName("com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.SpringDataMongo3Driver");
      return new String[]{"com.github.cloudyrock.spring.v5.MongockSpringDataV3CoreContext"};
    } catch (ClassNotFoundException e) {
      try {
        Class.forName("com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.SpringDataMongo3Driver");
        return new String[]{"com.github.cloudyrock.spring.v5.MongockSpringDataV2CoreContext"};
      } catch (ClassNotFoundException e2) {
        throw new ChangockException("\n\n" + ConfigErrorMessageUtils.DRIVER_NOT_FOUND_ERROR + "\n\n");

      }
    }
  }
}
