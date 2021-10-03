package io.mongock.runner.standalone;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.runner.standalone.base.migration.RunnerStandaloneBuilderBase;

public interface RunnerStandaloneBuilder extends RunnerStandaloneBuilderBase<RunnerStandaloneBuilder, ChangeEntry, MongockConfiguration> {
}
