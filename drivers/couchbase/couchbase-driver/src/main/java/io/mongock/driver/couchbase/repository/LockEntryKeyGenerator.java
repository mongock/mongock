package io.mongock.driver.couchbase.repository;

import io.mongock.driver.core.lock.LockEntry;

public class LockEntryKeyGenerator {

  public static final  String DOCUMENT_TYPE_LOCK_ENTRY = "mongockLockEntry";
  public String toKey(LockEntry lockEntry) {
    return toKey(lockEntry.getKey());
  }
  
  public String toKey(String key) {
    return new StringBuilder()
        .append(DOCUMENT_TYPE_LOCK_ENTRY)
        .append('-')
        .append(key)
        .toString();
  }
}
