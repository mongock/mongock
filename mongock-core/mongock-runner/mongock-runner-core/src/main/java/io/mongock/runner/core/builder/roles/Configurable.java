package io.mongock.runner.core.builder.roles;

import io.mongock.api.config.MongockConfiguration;

public interface Configurable<SELF extends Configurable<SELF, CONFIG>, CONFIG extends MongockConfiguration> {
  CONFIG getConfig();

  //TODO javadoc
  SELF setConfig(CONFIG config);
}
