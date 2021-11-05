package io.mongock.runner.core.internal;

import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.entry.ChangeType;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

public class ChangeSetItem {

  private final String id;

  private final String author;

  private final String order;

  private final boolean runAlways;

  private final String systemVersion;

  private final Method method;

  private final boolean failFast;

  private final boolean transactional;
  
  private final Method rollbackMethod;
  
  private final boolean beforeChangeSets;


  public ChangeSetItem(String id,
                       String author,
                       String order,
                       boolean runAlways,
                       String systemVersion,
                       boolean failFast,
                       boolean transactional,
                       Method changeSetMethod,
                       Method rollbackMethod,
                       boolean beforeChangeSets) {
    if (id == null || id.trim().isEmpty()) {
      throw new MongockException("id cannot be null or empty.");
    }
    else if (author == null || author.trim().isEmpty()) {
      throw new MongockException("author cannot be null or empty.");
    }
    this.id = id;
    this.author = author;
    this.order = order;
    this.runAlways = runAlways;
    this.systemVersion = systemVersion;
    this.method = changeSetMethod;
    this.failFast = failFast;
    this.transactional = transactional;
    this.rollbackMethod = rollbackMethod;
    this.beforeChangeSets = beforeChangeSets;
  }


  public String getId() {
    return id;
  }

  public String getAuthor() {
    return author;
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
  
  public boolean isBeforeChangeSets() {
    return beforeChangeSets;
  }

  public boolean isTransactional() {
    return transactional;
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
    sb.append(", transactional=").append(transactional);
    sb.append(", rollbackMethod=").append(rollbackMethod);
    sb.append(", beforeChangeSets=").append(beforeChangeSets);
    sb.append('}');
    return sb.toString();
  }

  public String toPrettyString() {
    return "ChangeEntry{" +
        "\"id\"=\"" + id + "\"" +
        ", \"author\"=\"" + author + "\"" +
        ", \"class\"=\"" + method.getDeclaringClass().getSimpleName() + "\"" +
        ", \"method\"=\"" + method.getName() + "\"" +
        '}';
  }


}
