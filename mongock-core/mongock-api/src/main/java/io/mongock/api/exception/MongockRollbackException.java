package io.mongock.api.exception;

/**
 *
 */
public class MongockRollbackException extends MongockException {

  private MongockException executionException;
  private MongockException rollbackException;

  public MongockRollbackException(MongockException executionException, MongockException rollbackException) {
    super();
    this.executionException = executionException;
    this.rollbackException = rollbackException;
  }

  public MongockException getExecutionException() {
    return executionException;
  }

  public MongockException getRollbackException() {
    return rollbackException;
  }
}
