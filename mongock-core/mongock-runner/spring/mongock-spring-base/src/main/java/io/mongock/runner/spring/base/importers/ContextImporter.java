package io.mongock.runner.spring.base.importers;

import java.util.List;

public interface ContextImporter {

  String[] getPaths();

  List<ArtifactDescriptor> getArtifacts();
}
