package io.mongock.runner.core.annotation;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import io.mongock.api.ChangeSetItem;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface LegacyAnnotationProcessor<CHANGESET extends ChangeSetItem> {

  default boolean isMethodAnnotatedAsChange(Method method) {
    return isChangeSet(method);
  }
  
  default boolean isChangeSet(Method method) {
      return method.isAnnotationPresent(ChangeSet.class);
  }


  default String getChangeLogOrder(Class<?> type) {
    return type.getAnnotation(ChangeLog.class).order();
  }

  default boolean isFailFast(Class<?> changeLogClass) {
    return changeLogClass.getAnnotation(ChangeLog.class).failFast();
  }

  /**
   * Returns the metatada associated to a method via a mongock change annotation, which includes
   * : ChangetSet, validation, undo, etc.
   * @param changeSetMethod
   * @return The metadata associated to a change method
   */
  default CHANGESET getChangePerformerItem(Method changeSetMethod) {
    return getChangePerformerItem(changeSetMethod, null);
  }


  CHANGESET getChangePerformerItem(Method changeSetMethod, Method rollbackMethod);

  default String getId(Method method) {
    return getChangePerformerItem(method).getId();
  }

}
