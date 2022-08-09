package io.mongock.runner.core.internal;

import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.entry.ChangeType;
import io.mongock.driver.api.util.ChangePrintable;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

import static io.mongock.driver.api.entry.ChangeType.EXECUTION;

public class ChangeSetItem implements ChangePrintable {

  private final String id;

  private final String author;

  private final String order;

  private final boolean runAlways;

  private final String systemVersion;

  private final Method method;

  private final boolean failFast;
  
  private final Method rollbackMethod;
  
  private boolean system;

  public ChangeSetItem(String id,
                       String author,
                       String order,
                       boolean runAlways,
                       String systemVersion,
                       boolean failFast,
                       Method changeSetMethod,
                       Method rollbackMethod,
                       boolean system) {
    if (id == null || id.trim().isEmpty()) {
      throw new MongockException("id cannot be null or empty.");
    } else if (author == null) {
      throw new MongockException("author cannot be null.");
    }
    this.id = id;
    this.author = author;
    this.order = order;
    this.runAlways = runAlways;
    this.systemVersion = systemVersion;
    this.method = changeSetMethod;
    this.failFast = failFast;
    this.rollbackMethod = rollbackMethod;
    this.system = system;
  }


  @Override
  public String getId() {
    return id;
  }

  @Override
  public ChangeType getType() {
    return EXECUTION;
  }

  @Override
  public String getAuthor() {
    return author;
  }

  @Override
  public String getChangeLogClassString() {
    return method.getDeclaringClass().getSimpleName();
  }

  @Override
  public String getMethodNameString() {
    return method.getName();
  }

  public String getOrder() {
    return order;
  }

  public boolean isRunAlways() {
    return runAlways;
  }

  public String getSystemVersion() {
    return systemVersion;
  }

  public Method getMethod() {
    return method;
  }

  public boolean isFailFast() {
    return failFast;
  }

  public Optional<Method> getRollbackMethod() {
    return Optional.ofNullable(rollbackMethod);
  }
  
  public boolean isSystem() {
    return system;
  }
  
  public void setSystem(boolean system) {
    this.system = system;
  }
  
  public boolean isBeforeChangeSets() {
    return false;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangeSetItem that = (ChangeSetItem) o;
    return id.equals(that.id) && Objects.equals(author, that.author);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, author);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ChangeSetItem{");
    sb.append("id='").append(id).append('\'');
    sb.append(", author='").append(author).append('\'');
    sb.append(", order='").append(order).append('\'');
    sb.append(", runAlways=").append(runAlways);
    sb.append(", systemVersion='").append(systemVersion).append('\'');
    sb.append(", method=").append(method);
    sb.append(", failFast=").append(failFast);
    sb.append(", rollbackMethod=").append(rollbackMethod);
    sb.append(", system=").append(system);
    sb.append('}');
    return sb.toString();
  }



}
