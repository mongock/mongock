package com.github.cloudyrock.mongock;

/**
 * @author abelski
 */
public class MongockException extends RuntimeException {
  MongockException(String message) {
    super(message);
  }

  MongockException(String message, Exception e) {
    super(message, e);
  }
}
