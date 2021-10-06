package io.mongock.test.util.decorator;

import java.util.ArrayList;

public class DecoratorTestCollection extends ArrayList<DecoratorDefinition> {

  public DecoratorTestCollection() {
  }

  public DecoratorTestCollection(DecoratorTestCollection initial) {
    super(initial);
  }

  public <T> DecoratorTestCollection addDecorator(Class<T> interfaceType, Class<? extends T> implementingClass) {
    this.add(new DecoratorDefinition(interfaceType, implementingClass));
    return this;
  }


  public DecoratorTestCollection addRawDecorator(Class interfaceType, Class<?> implementingClass) {
    this.add(new DecoratorDefinition(interfaceType, implementingClass));
    return this;
  }

  public boolean contains(Class interfaceType, Class<?> implementingClass) {
    return this.contains(new DecoratorDefinition(interfaceType, implementingClass));
  }


}
