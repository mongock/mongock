package io.mongock.runner.core.builder.roles;

import io.mongock.api.config.MongockConfiguration;

import java.util.Map;

public interface MigrationWriter<SELF extends MigrationWriter<SELF, CONFIG>, CONFIG extends MongockConfiguration>
    extends Configurable<SELF, CONFIG>, SelfInstanstiator<SELF> {
  /**
   * Indicates if the ignored changeSets should be tracked or not
   *
   * @param trackIgnored if the ignored changeSets should be tracked
   * @return builder for fluent interface
   */
  default SELF setTrackIgnored(boolean trackIgnored) {
    getConfig().setTrackIgnored(trackIgnored);
    return getInstance();
  }


  /**
   * Set the metadata for the Mongock process. This metadata will be added to each document in the MongockChangeLog
   * collection. This is useful when the system needs to add some extra info to the changeLog.
   * <b>Optional</b> Default value empty Map
   *
   * @param metadata Custom metadata object  to be added
   * @return builder for fluent interface
   */
  default SELF withMetadata(Map<String, Object> metadata) {
    getConfig().setMetadata(metadata);
    return getInstance();
  }
}
