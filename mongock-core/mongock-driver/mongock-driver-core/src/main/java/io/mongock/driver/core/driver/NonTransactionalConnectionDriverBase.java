package io.mongock.driver.core.driver;

import io.mongock.driver.api.driver.Transactional;

import java.util.Optional;

public abstract class NonTransactionalConnectionDriverBase extends ConnectionDriverBase {

  protected NonTransactionalConnectionDriverBase(long lockAcquiredForMillis,
                                                 long lockQuitTryingAfterMillis,
                                                 long lockTryFrequencyMillis) {
    super(lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
  }

  public Optional<Transactional> getTransactioner() {
    return Optional.empty();
  }

}
