package io.mongock.runner.core.executor.operation;

import java.util.Objects;

public abstract class Operation {

  private final String id;

  public Operation(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Operation)) return false;
    Operation operation = (Operation) o;
    return id.equals(operation.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
