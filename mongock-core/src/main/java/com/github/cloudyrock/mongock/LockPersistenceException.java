package com.github.cloudyrock.mongock;

/**
 *
 * @since 04/04/2018
 */
class LockPersistenceException extends RuntimeException {

  private static final long serialVersionUID = -4232386506613422980L;

  LockPersistenceException(String msg) {
    super(msg);
  }

  LockPersistenceException(MongockException ex) {
    super(ex);
  }
}
