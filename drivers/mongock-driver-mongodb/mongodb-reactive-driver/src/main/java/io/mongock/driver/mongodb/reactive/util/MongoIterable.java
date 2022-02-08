package io.mongock.driver.mongodb.reactive.util;

import java.util.ArrayList;

public class MongoIterable<T> extends ArrayList<T> {

  public T first() {
    if(size() > 0) {
      return get(0);
    } else {
      return null;
    }
  }
}
