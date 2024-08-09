package io.mongock.runner.core.builder.roles;

import io.mongock.api.config.MongockConfiguration;

//setTransactionEnable will be moved to driver
@Deprecated
public interface Transactioner<SELF extends Transactioner<SELF, CONFIG>, CONFIG extends MongockConfiguration>
    extends Configurable<SELF, CONFIG>, SelfInstanstiator<SELF> {
  /**
   * @deprecated  As of release 5.5, replaced by {@link #setTransactional}
   * 
   * Indicates if Mongock should run in transaction mode or not
   *
   * @param transactionEnabled if Mongock should run in transaction mode
   * @return builder for fluent interface
   */
  @Deprecated
  default SELF setTransactionEnabled(boolean transactionEnabled) {
    getConfig().setTransactionEnabled(transactionEnabled);
    return getInstance();
  }
  
  /**
   * Indicates if Mongock should run in transaction mode or not
   *
   * @param transactional if Mongock should run in transaction mode
   * @return builder for fluent interface
   */
  default SELF setTransactional(boolean transactional) {
    getConfig().setTransactional(transactional);
    return getInstance();
  }
}
