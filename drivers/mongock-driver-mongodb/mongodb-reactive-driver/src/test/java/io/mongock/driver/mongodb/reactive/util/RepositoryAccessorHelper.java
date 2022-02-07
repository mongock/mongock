package io.mongock.driver.mongodb.reactive.util;

import io.mongock.driver.mongodb.reactive.repository.MongoReactiveRepositoryBase;

import java.lang.reflect.Field;

public class RepositoryAccessorHelper {

  private static final Field collectionField;

  static {
    try {
      collectionField = MongoReactiveRepositoryBase.class.getDeclaredField("collection");
      collectionField.setAccessible(true);
    }
    catch (NoSuchFieldException e) {
      throw  new RepositoryAccessorException(e);
    }
  }


  public static MongoCollectionSync getCollection(MongoReactiveRepositoryBase repo) {
    try {
      return (MongoCollectionSync) collectionField.get(repo);
    } catch (IllegalAccessException e) {
      throw  new RepositoryAccessorException(e);
    }
  }



}
