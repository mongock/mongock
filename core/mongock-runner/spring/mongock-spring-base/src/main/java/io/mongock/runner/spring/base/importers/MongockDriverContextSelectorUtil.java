package io.mongock.runner.spring.base.importers;

import io.mongock.api.exception.MongockException;

import java.util.List;
import java.util.Objects;

public class MongockDriverContextSelectorUtil {

  private final static String DRIVER_NOT_FOUND_ERROR_TEMPLATE = "MONGOCK DRIVER HAS NOT BEEN IMPORTED" +
      "\n====================================" +
      "\n\tSOLUTION: You need to import one of the following artifacts";
  
  private static String generateDriverNotFoundError (List<ContextImporter> contextImporters) {
    StringBuilder sb = new StringBuilder(DRIVER_NOT_FOUND_ERROR_TEMPLATE);
    contextImporters.stream()
        .map(ContextImporter::getArtifacts)
        .flatMap(List::stream)
        .forEach(desc -> sb.append("\n\t- '").append(desc.getArtifact()).append("' for ").append(desc.getTitle()));
    return sb.toString();
  }
  
  public static String[] selectImports(List<ContextImporter> contextImporters) {
    return contextImporters.stream()
        .map(contextImporter -> contextImporter.getPaths())
        .filter(Objects::nonNull)
        .findFirst()
        .orElseThrow(() -> new MongockException(String.format("\n\n%s\n\n", generateDriverNotFoundError(contextImporters))));
  }
  


}
