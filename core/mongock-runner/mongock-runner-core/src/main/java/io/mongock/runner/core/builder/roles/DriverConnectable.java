package io.mongock.runner.core.builder.roles;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.api.entry.ChangeEntry;

public interface DriverConnectable<SELF extends DriverConnectable<SELF, CHANGE_ENTRY, CONFIG>, CHANGE_ENTRY extends ChangeEntry, CONFIG extends MongockConfiguration>
    extends Configurable<SELF, CONFIG>, SelfInstanstiator<SELF> {
  /**
   * Set the specific connection driver
   * <b>Mandatory</b>
   *
   * @param driver connection driver
   * @return builder for fluent interface
   */
  SELF setDriver(ConnectionDriver<CHANGE_ENTRY> driver);

  /**
   * Indicates that in case the lock cannot be obtained, therefore the migration is not executed, Mongock won't throw
   * any exception and the application will carry on.
   * <p>
   * Only set this to false if the changes are not mandatory and the application can work without them. Leave it true otherwise.
   * <b>Optional</b> Default value true.
   *
   * @return builder for fluent interface
   */
  default SELF dontFailIfCannotAcquireLock() {
    getConfig().setThrowExceptionIfCannotObtainLock(false);
    return getInstance();
  }
}
