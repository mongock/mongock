package io.mongock.driver.api.util;

import io.mongock.driver.api.entry.ChangeState;
import io.mongock.driver.api.entry.ChangeType;

import static io.mongock.driver.api.entry.ChangeType.BEFORE_EXECUTION;

public interface ChangePrintable {

  String getId();

  ChangeType getType();

  String getAuthor();

  String getChangeLogClassString();

  String getMethodNameString();

  default String toPrettyString() {
    String type = getType() == BEFORE_EXECUTION ? "before-execution" : "execution";
    return "{" +
        "\"id\"=\"" + getId() + "\"" +
        ", \"type\"=\"" + type + "\"" +
        ", \"author\"=\"" + getAuthor() + "\"" +
        ", \"class\"=\"" + getChangeLogClassString() + "\"" +
        ", \"method\"=\"" + getMethodNameString() + "\"" +
        '}';
  }

}
