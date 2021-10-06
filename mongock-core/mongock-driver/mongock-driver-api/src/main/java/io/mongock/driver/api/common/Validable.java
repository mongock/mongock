package io.mongock.driver.api.common;

import io.mongock.api.exception.MongockException;

public interface Validable {

  void runValidation() throws MongockException;
}
