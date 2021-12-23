package io.mongock.driver.core.driver;

import io.mongock.driver.api.driver.Transactionable;

public abstract class TransactionableConnectionDriverBase extends ConnectionDriverBase implements Transactionable {


  protected boolean transactionEnabled ;

  protected TransactionableConnectionDriverBase(long lockAcquiredForMillis,
                                                long lockQuitTryingAfterMillis,
                                                long lockTryFrequencyMillis,
                                                boolean defaultTransactionEnabled) {
    super(lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
    transactionEnabled = defaultTransactionEnabled;
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
  public void executeInTransaction(Runnable operation) {

  }













}
