package com.github.cloudyrock.mongock;

/**
 * @author abelski
 */
public class MongockException extends Exception {
  MongockException(String message) {
    super(message);
  }

  public MongockException(String message, Exception e) {
    super(message, e);
  }
}
