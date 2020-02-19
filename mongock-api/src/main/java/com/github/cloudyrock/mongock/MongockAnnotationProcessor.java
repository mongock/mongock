package com.github.cloudyrock.mongock;

import io.changock.migration.api.ChangeSetItem;
import io.changock.migration.api.ChangockAnnotationProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

public class MongockAnnotationProcessor extends ChangockAnnotationProcessor {
  @Override
  public Collection<Class<? extends Annotation>> getChangeLogAnnotationClass() {
    Collection<Class<? extends Annotation>> changeLogClasses = Arrays.asList(ChangeLog.class);
    changeLogClasses.addAll(super.getChangeLogAnnotationClass());
    return changeLogClasses;
  }

  @Override
  public String getChangeLogOrder(Class<?> type) {
    try {
      return super.getChangeLogOrder(type);
    } catch (Exception ex) {
      return type.getAnnotation(ChangeLog.class).order();
    }
  }

  public ChangeSetItem getChangeSet(Method method) {
    try {
      return super.getChangeSet(method);
    } catch (Exception ex) {
      ChangeSet ann = method.getAnnotation(ChangeSet.class);
      return new ChangeSetItem(ann.id(), ann.author(), ann.order(), ann.runAlways(), ann.systemVersion(), method);
    }

  }
}
