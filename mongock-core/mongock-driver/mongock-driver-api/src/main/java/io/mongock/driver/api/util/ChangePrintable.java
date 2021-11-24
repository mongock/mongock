package io.mongock.driver.api.util;

public interface ChangePrintable {

  String getId();

  String getTypeString();

  String getAuthor();

  String getChangeLogClassString();

  String getMethodNameString();

  default String toPrettyString() {
    return "{" +
        "\"id\"=\"" + getId() + "\"" +
        ", \"type\"=\"" + getTypeString() + "\"" +
        ", \"author\"=\"" + getAuthor() + "\"" +
        ", \"class\"=\"" + getChangeLogClassString() + "\"" +
        ", \"method\"=\"" + getMethodNameString() + "\"" +
        '}';
  }

}
