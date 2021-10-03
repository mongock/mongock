package io.mongock.runner.springboot;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.runner.springboot.base.builder.migration.RunnerSpringbootBuilderBase;

//TODO javadoc
public interface RunnerSpringbootBuilder extends RunnerSpringbootBuilderBase<RunnerSpringbootBuilder, ChangeEntry, MongockConfiguration> {
}
