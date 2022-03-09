package io.mongock.runner.core.executor.operation.migrate;

import io.mongock.runner.core.executor.operation.Operation;

public class MigrateAllOperation extends Operation {

  public static final String ID = "MIGRATE_ALL";

  public MigrateAllOperation() {
    super(ID);
  }


}
