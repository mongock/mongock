package io.mongock.util.test;

public class ExpectedException extends RuntimeException {

  public ExpectedException(String msg) {super(msg);}


  public ExpectedException() {super("EXPECTED EXCEPTION");}
}
