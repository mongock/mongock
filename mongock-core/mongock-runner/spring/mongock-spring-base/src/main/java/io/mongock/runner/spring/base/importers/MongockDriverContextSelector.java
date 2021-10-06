package io.mongock.runner.spring.base.importers;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Collections;

public class MongockDriverContextSelector implements ImportSelector {

  @Override
  public String[] selectImports(AnnotationMetadata importingClassMetadata) {
    return MongockDriverContextSelectorUtil.selectImports(Collections.singletonList(
      new MongoSpringDataImporter()
    ));
  }

}
