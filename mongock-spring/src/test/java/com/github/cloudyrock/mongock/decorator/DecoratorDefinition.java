package com.github.cloudyrock.mongock.decorator;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DecoratorDefinition {


  private final Class interfaceType;
  private final Class implementingType;
  private final Object instance;
  private final List<String> noLockGardMethods;

  public <T> DecoratorDefinition(Class<T> interfaceType, Class<? extends T> implementingType) {
    this(interfaceType, implementingType, null, new String[]{});
  }

  public <T, R extends T> DecoratorDefinition(Class<T> interfaceType, Class<R> implementingType, R instance) {
    this(interfaceType, implementingType, instance, new String[]{});
  }

  public <T, R extends T> DecoratorDefinition(Class<T> interfaceType, Class<R> implementingType, String... noLockGardMethods) {
    this(interfaceType, implementingType, null, noLockGardMethods);
  }

  public <T, R extends T> DecoratorDefinition(Class<T> interfaceType, Class<R> implementingType, R instance, String... noLockGardMethods) {
    this.interfaceType = interfaceType;
    this.implementingType = implementingType;
    this.instance = instance;
    this.noLockGardMethods = Arrays.asList(noLockGardMethods);
  }

  public Class getInterfaceType() {
    return interfaceType;
  }

  public Class getImplementingType() {
    return implementingType;
  }

  public Optional<Object> getInstance() {
    return Optional.ofNullable(instance);
  }

  public List<String> getNoLockGardMethods() {
    return noLockGardMethods;
  }
}
