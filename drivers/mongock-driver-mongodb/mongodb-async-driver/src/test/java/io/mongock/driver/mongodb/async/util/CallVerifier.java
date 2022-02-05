package io.mongock.driver.mongodb.async.util;

public interface CallVerifier {

  int getCounter();

  void increaseCounter();
}
