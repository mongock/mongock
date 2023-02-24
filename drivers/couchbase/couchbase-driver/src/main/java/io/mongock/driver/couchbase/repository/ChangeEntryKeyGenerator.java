package io.mongock.driver.couchbase.repository;

import io.mongock.driver.api.entry.ChangeEntry;

public class ChangeEntryKeyGenerator {

  public static final String KEY_PREFIX = "mongock-";
  public String toKey(ChangeEntry changeEntry) {
    return new StringBuilder()
        .append(KEY_PREFIX)
        .append(changeEntry.getExecutionId())
        .append('-')
        .append(changeEntry.getAuthor())
        .append('-')
        .append(changeEntry.getChangeId()).toString();
  }
}
