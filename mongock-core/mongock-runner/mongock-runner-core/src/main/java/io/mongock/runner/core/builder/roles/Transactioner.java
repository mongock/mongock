package io.mongock.runner.core.builder.roles;

import io.mongock.api.config.MongockConfiguration;

//setTransactionEnable will be moved to driver
@Deprecated
public interface Transactioner<SELF extends Transactioner<SELF, CONFIG>, CONFIG extends MongockConfiguration>
    extends Configurable<SELF, CONFIG>, SelfInstanstiator<SELF> {
  /**
   * Indicates if Mongock should run in transaction mode or not
   *
   * @param transactionEnabled if Mongock should run in transaction mode
   * @return builder for fluent interface
   */
  default SELF setTransactionEnabled(boolean transactionEnabled) {
    getConfig().setTransactionEnabled(transactionEnabled);
    return getInstance();
  }
}
