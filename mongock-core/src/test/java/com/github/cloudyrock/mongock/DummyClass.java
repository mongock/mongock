package com.github.cloudyrock.mongock;

public class DummyClass {
  private final String value;

  DummyClass(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void throwException() throws DummyException {
    throw new DummyException();
  }

  public static class DummyException extends Exception {
  }

}
