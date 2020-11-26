package com.github.cloudyrock.spring.v5;

import com.github.cloudyrock.spring.v5.importers.ContextImporter;
import com.github.cloudyrock.spring.v5.importers.MongoSpringDataImporter;
import io.changock.migration.api.exception.ChangockException;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MongockCoreContextSelector implements ImportSelector {


  private final static String DRIVER_NOT_FOUND_ERROR = "MONGOCK DRIVER HAS NOT BEEN IMPORTED" +
      "\n====================================" +
      "\n\tSOLUTION: You need to import one of these artifacts" +
      "\n\t- 'mongodb-springdata-v3-driver' for springdata 3" +
      "\n\t- 'mongodb-springdata-v2-driver' for springdata 2";

  private final List<ContextImporter> contextImporters = Arrays.asList(
    new MongoSpringDataImporter()
  );


  @Override
  public String[] selectImports(AnnotationMetadata importingClassMetadata) {
    return contextImporters.stream()
        .map(ContextImporter::getPaths)
        .filter(Objects::nonNull)
        .findFirst()
        .orElseThrow(() ->  new ChangockException(String.format("\n\n%s\n\n", DRIVER_NOT_FOUND_ERROR)));
  }

}
