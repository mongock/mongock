package io.mongock.driver.core.driver;

import io.mongock.driver.api.driver.Transactional;

public abstract class TransactionalConnectionDriverBase extends ConnectionDriverBase implements Transactional {


  protected boolean transactionEnabled = true;

  protected TransactionalConnectionDriverBase(long lockAcquiredForMillis,
                                              long lockQuitTryingAfterMillis,
                                              long lockTryFrequencyMillis) {
    super(lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
  }

  @Override
  public void disableTransaction() {
    transactionEnabled = false;
  }

  @Override
  public void enableTransaction() {
    transactionEnabled = true;
  }

  @Override
  public abstract void executeInTransaction(Runnable operation);

}
