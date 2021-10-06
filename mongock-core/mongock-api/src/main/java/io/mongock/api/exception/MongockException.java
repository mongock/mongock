package io.mongock.api.exception;

/**
 *
 */
public class MongockException extends RuntimeException {

  public MongockException() {
    super();
  }

  public MongockException(Throwable exception) {
    super(exception);
  }

  public MongockException(String message) {
    super(message);
  }

  public MongockException(String formattedMessage, Object... args) {
    super(String.format(formattedMessage, args));
  }

  public MongockException(String message, Exception e) {
    super(message, e);
  }
}
