package io.mongock.runner.core.internal;

import java.lang.reflect.Method;

public class BeforeChangeSetItem extends ChangeSetItem{
  public BeforeChangeSetItem(String baseId,
                             String author,
                             String order,
                             boolean runAlways,
                             String systemVersion,
                             boolean failFast,
                             Method changeSetMethod,
                             Method rollbackMethod) {
    super(String.format("%s_%s", baseId, "before"), author, order, runAlways, systemVersion, failFast, changeSetMethod, rollbackMethod);
  }
}
