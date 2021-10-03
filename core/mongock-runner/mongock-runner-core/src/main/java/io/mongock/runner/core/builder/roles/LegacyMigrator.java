package io.mongock.runner.core.builder.roles;

import io.mongock.api.config.LegacyMigration;
import io.mongock.api.config.MongockConfiguration;

public interface LegacyMigrator<SELF extends LegacyMigrator<SELF, CONFIG>, CONFIG extends MongockConfiguration>
    extends Configurable<SELF, CONFIG>, SelfInstanstiator<SELF> {
  /**
   * Adds a legacy migration to be executed before the actual migration
   *
   * @param legacyMigration represents the legacy migration
   * @return builder for fluent interface
   */
  default SELF setLegacyMigration(LegacyMigration legacyMigration) {
    getConfig().setLegacyMigration(legacyMigration);
    return getInstance();
  }
}
