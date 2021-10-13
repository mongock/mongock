package io.mongock.runner.core.builder;

import io.mongock.runner.core.builder.RunnerBuilder;

public interface RunnerBuilderProvider {

  RunnerBuilder getBuilder();
}
