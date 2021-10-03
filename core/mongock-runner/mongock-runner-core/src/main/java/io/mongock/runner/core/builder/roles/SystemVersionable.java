package io.mongock.runner.core.builder.roles;

import io.mongock.api.config.MongockConfiguration;

public interface SystemVersionable<SELF extends SystemVersionable<SELF, CONFIG>, CONFIG extends MongockConfiguration>
    extends Configurable<SELF, CONFIG>, SelfInstanstiator<SELF> {
  /**
   * Set up the start Version for versioned schema changes.
   * This shouldn't be confused with a supposed change version(Notice, currently changeSet doesn't have version).
   * This is from a consultancy point of view. So the changeSet are tagged with a systemVersion and then when building
   * Mongock, you specify the systemVersion range you want to apply, so all the changeSets tagged with systemVersion
   * inside that range will be applied
   * <b>Optional</b> Default value 0
   *
   * @param startSystemVersion Version to start with
   * @return builder for fluent interface
   */
  default SELF setStartSystemVersion(String startSystemVersion) {
    getConfig().setStartSystemVersion(startSystemVersion);
    return getInstance();
  }

  /**
   * Set up the end Version for versioned schema changes.
   * This shouldn't be confused with the changeSet systemVersion. This is from a consultancy point of view.
   * So the changeSet are tagged with a systemVersion and then when building Mongock, you specify
   * the systemVersion range you want to apply, so all the changeSets tagged with systemVersion inside that
   * range will be applied.
   * <b>Optional</b> Default value string value of MAX_INTEGER
   *
   * @param endSystemVersion Version to end with
   * @return builder for fluent interface
   */
  default SELF setEndSystemVersion(String endSystemVersion) {
    getConfig().setEndSystemVersion(endSystemVersion);
    return getInstance();
  }
}
