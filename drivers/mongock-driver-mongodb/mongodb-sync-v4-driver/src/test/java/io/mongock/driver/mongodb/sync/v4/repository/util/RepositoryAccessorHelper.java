package io.mongock.driver.mongodb.sync.v4.repository.util;

import io.mongock.driver.mongodb.sync.v4.repository.MongoSync4RepositoryBase;
import com.mongodb.client.MongoCollection;

import java.lang.reflect.Field;

public class RepositoryAccessorHelper {

  private static final Field collectionField;

  static {
    try {
      collectionField = MongoSync4RepositoryBase.class.getDeclaredField("collection");
      collectionField.setAccessible(true);
    }
    catch (NoSuchFieldException e) {
      throw  new RepositoryAccessorException(e);
    }
  }


  public static MongoCollection  getCollection(MongoSync4RepositoryBase repo) {
    try {
      return (MongoCollection) collectionField.get(repo);
    } catch (IllegalAccessException e) {
      throw  new RepositoryAccessorException(e);
    }
  }



}
