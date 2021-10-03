package io.mongock.runner.core.builder.roles;

import io.mongock.api.config.MongockConfiguration;

public interface ServiceIdentificable<SELF extends ServiceIdentificable<SELF, CONFIG>, CONFIG extends MongockConfiguration>
    extends Configurable<SELF, CONFIG>, SelfInstanstiator<SELF> {
  /**
   * Set up the name of the service running mongock.
   * This will be used as a suffix to the hostname when saving changelogs history in database.
   * <b>Optional</b> Default value null
   *
   * @param serviceIdentifier Identifier of the service running mongock
   * @return builder for fluent interface
   */
  default SELF setServiceIdentifier(String serviceIdentifier) {
    getConfig().setServiceIdentifier(serviceIdentifier);
    return getInstance();
  }
}
