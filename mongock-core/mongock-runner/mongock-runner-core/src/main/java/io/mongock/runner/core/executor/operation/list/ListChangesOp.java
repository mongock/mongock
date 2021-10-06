package io.mongock.runner.core.executor.operation.list;

import io.mongock.runner.core.executor.operation.Operation;

public class ListChangesOp extends Operation {

  public static final String ID = "LIST";

  public ListChangesOp() {
    super(ID);
  }
}
