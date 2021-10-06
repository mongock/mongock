package io.mongock.utils.field;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FieldUtil {

  private FieldUtil() {
  }

  public static List<Field> getAllFields(Class<?> clazz) {
    if (Object.class.equals(clazz)) {
      return Collections.emptyList();
    }
    List<Field> allFields = new ArrayList<>(getAllFields(clazz.getSuperclass()));
    List<Field> fields = Stream.of(clazz.getDeclaredFields())
        .filter(field -> field.isAnnotationPresent(io.mongock.utils.field.Field.class))
        .collect(Collectors.toList());
    allFields.addAll(fields);
    return allFields;
  }
}
