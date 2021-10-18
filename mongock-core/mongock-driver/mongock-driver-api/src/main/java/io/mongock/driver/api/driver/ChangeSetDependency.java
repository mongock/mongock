package io.mongock.driver.api.driver;

import io.mongock.api.exception.MongockException;

import java.util.Objects;

public class ChangeSetDependency {

  public static final String DEFAULT_NAME = "default_name_not_used";

  private String name;
  private Class<?> type;
  private boolean proxeable;
  protected Object instance;


  public ChangeSetDependency(Object instance) {
    this(instance.getClass(), instance);
  }

  public ChangeSetDependency(Class<?> type, Object instance) {
    this(type, instance, true);
  }
  
  public ChangeSetDependency(Class<?> type, Object instance, boolean proxeable) {
    this(DEFAULT_NAME, type, instance, proxeable);
  }
  
  public ChangeSetDependency(String name, Class<?> type, Object instance) {
    this(name, type, instance, true);
  }

  public ChangeSetDependency(String name, Class<?> type, Object instance, boolean proxeable) {
    this(name, type, proxeable);
    if (instance == null) {
      throw new MongockException("dependency instance cannot be null");
    }
    this.instance = instance;
  }

  protected ChangeSetDependency(String name, Class<?> type, boolean proxeable) {
    checkParameters(name, type);
    this.name = name;
    this.type = type;
    this.proxeable = proxeable;
  }


  private void checkParameters(String name, Class<?> type) {
    if (name == null || name.isEmpty()) {
      throw new MongockException("dependency name cannot be null/empty");
    }
    if (type == null) {
      throw new MongockException("dependency type cannot be null");
    }
  }

  public String getName() {
    return name;
  }

  public Class<?> getType() {
    return type;
  }

  public Object getInstance() {
    return instance;
  }

  public boolean isDefaultNamed() {
    return DEFAULT_NAME.equals(name);
  }
  
  public boolean isProxeable() {
    return proxeable;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ChangeSetDependency)) return false;
    ChangeSetDependency that = (ChangeSetDependency) o;
    return name.equals(that.name) && type.equals(that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, type);
  }
}
