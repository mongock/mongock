package io.mongock.driver.core.driver;

import io.mongock.driver.api.driver.Transactional;

import java.util.Optional;

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
  public final Optional<Transactional> getTransactioner() {
    return Optional.ofNullable(transactionEnabled ? this : null);
  }

  @Override
  public abstract void executeInTransaction(Runnable operation);

}
