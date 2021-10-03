package io.mongock.runner.core.changelogs.instantiator.good;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;

@ChangeLog
public class ChangeLogCustomConstructor {
  private String stringValue;
  private int intValue;

  public ChangeLogCustomConstructor(String stringValue, int integerValue) {
    this.stringValue = stringValue;
    this.intValue = integerValue;
  }

  public String getStringValue() {
    return stringValue;
  }

  public int getIntegerValue() {
    return intValue;
  }

  @ChangeSet(order = "001", id = "test", author = "test")
  public void migration() {
  }
}
