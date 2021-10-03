package io.mongock.driver.api.lock;

public class LockCheckException extends RuntimeException {
  public LockCheckException(String s) {
    super(s);
  }

  public LockCheckException() {
    super();
  }
}
