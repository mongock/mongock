package com.github.cloudyrock.mongock.driver.mongodb.test.template.util;

public class CallVerifierImpl implements CallVerifier{
    private int counter = 0;

  @Override
  public int getCounter() {
    return counter;
  }

  @Override
  public void increaseCounter() {
    counter++;
  }
}
