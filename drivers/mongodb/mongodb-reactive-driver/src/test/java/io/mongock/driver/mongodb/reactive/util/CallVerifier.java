package io.mongock.driver.mongodb.reactive.util;

public interface CallVerifier {

  int getCounter();

  void increaseCounter();
}
