package io.mongock.runner.core.annotation;

import com.github.cloudyrock.mongock.ChangeSet;
import io.mongock.runner.core.internal.ChangeSetItem;

import java.lang.reflect.Method;

public class LegacyLegacyAnnotationProcessor implements LegacyAnnotationProcessor {

  @Override
  public ChangeSetItem getChangePerformerItem(Method changeSetMethod, Method rollbackMethod) {
    return getChangeSetItem(changeSetMethod, rollbackMethod);
  }

  public ChangeSetItem getChangeSetItem(Method method, Method rollbackMethod) {
      ChangeSet ann = method.getAnnotation(ChangeSet.class);
      return createChangeSetItemInstance(ann.id(), ann.author(), ann.order(), ann.runAlways(), ann.systemVersion(), ann.failFast(), method, rollbackMethod);
  }

  private ChangeSetItem createChangeSetItemInstance(String id, String author, String order, boolean runAlways, String systemVersion, boolean failFast, Method method, Method rollbackMethod) {
    return new ChangeSetItem(id, author, order, runAlways, systemVersion, failFast, method, rollbackMethod);
  }

}
