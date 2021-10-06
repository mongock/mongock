package io.mongock.test.util.decorator;


import java.util.Objects;

public class DecoratorDefinition {


  private final Class<?> interfaceType;
  private final Class<?> implementingType;

  public <T, R extends T> DecoratorDefinition(Class<T> interfaceType, Class<R> implementingType) {
    this.interfaceType = interfaceType;
    this.implementingType = implementingType;
  }

  public Class<?> getInterfaceType() {
    return interfaceType;
  }

  public Class<?> getImplementingType() {
    return implementingType;
  }



  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DecoratorDefinition that = (DecoratorDefinition) o;
    return interfaceType.equals(that.interfaceType) &&
        implementingType.equals(that.implementingType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(interfaceType, implementingType);
  }
}
