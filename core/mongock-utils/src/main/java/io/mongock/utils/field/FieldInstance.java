package io.mongock.utils.field;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class FieldInstance {


  private final Field field;
  private final Object object;
  private final boolean isEnum;

  public FieldInstance(Field field, Object object) {
    field.setAccessible(true);
    this.field = field;
    this.object = object;
    this.isEnum = field.getType().isEnum();
  }

  public String getName() {
    return field.getAnnotation(io.mongock.utils.field.Field.class).value();
  }

  public Object getValue() {
    try {
      Object value = field.get(object);
      return !isEnum ? value : field.getType().getMethod("name").invoke(value);
    } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
}
