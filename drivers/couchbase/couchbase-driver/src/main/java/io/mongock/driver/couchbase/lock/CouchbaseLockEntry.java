package io.mongock.driver.couchbase.lock;

import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonCreator;
import com.couchbase.client.java.json.JsonObject;
import io.mongock.driver.core.lock.LockEntry;
import io.mongock.driver.couchbase.repository.CouchbaseRepositoryBase;

import java.util.Date;

/**
 * LockEntry implementation for Couchbase, basically adds a way to deserialize the object from JSON.
 *
 * @author Tigran Babloyan
 */
public class CouchbaseLockEntry extends LockEntry {
  private final String docType;
  @JsonCreator
  public CouchbaseLockEntry(JsonObject jsonObject) {
    super(jsonObject.getString(LockEntry.KEY_FIELD),
          jsonObject.getString(LockEntry.STATUS_FIELD),
          jsonObject.getString(LockEntry.OWNER_FIELD),
          new Date(jsonObject.getLong(LockEntry.EXPIRES_AT_FIELD)));
    this.docType = jsonObject.getString(CouchbaseRepositoryBase.DOCUMENT_TYPE_KEY);
  }

  public String getDocType() {
    return docType;
  }
}
