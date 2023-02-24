package io.mongock.driver.couchbase.util;

import com.couchbase.client.core.io.CollectionIdentifier;

public final class CollectionIdentifierUtil {
  private CollectionIdentifierUtil(){
    // nothing to do
  }

  public static boolean isDefaultCollection(String scope, String collection) {
    return CollectionIdentifier.DEFAULT_SCOPE.equals(scope)
        && CollectionIdentifier.DEFAULT_COLLECTION.equals(collection);
  }
}
