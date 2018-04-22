package com.github.cloudyrock.mongock;

class LockCheckException extends RuntimeException {
  LockCheckException(String s) {
    super(s);
  }

  LockCheckException(InterruptedException ex) {
    super(ex);
  }

  LockCheckException() {
    super();
  }
}
