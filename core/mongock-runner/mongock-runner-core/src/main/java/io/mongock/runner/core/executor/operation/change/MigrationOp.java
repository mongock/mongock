package io.mongock.runner.core.executor.operation.change;

import io.mongock.runner.core.executor.operation.Operation;

public class MigrationOp extends Operation {

  public static final String ID = "MIGRATION";

  public MigrationOp() {
    super(ID);
  }


}
