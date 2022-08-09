package io.mongock.runner.core.internal;

import io.mongock.driver.api.entry.ChangeType;
import java.lang.reflect.Method;

import static io.mongock.driver.api.entry.ChangeType.BEFORE_EXECUTION;

public class BeforeChangeSetItem extends ChangeSetItem{
  public BeforeChangeSetItem(String baseId,
                             String author,
                             String order,
                             boolean runAlways,
                             String systemVersion,
                             boolean failFast,
                             Method changeSetMethod,
                             Method rollbackMethod,
                             boolean system) {
    super(String.format("%s_%s", baseId, "before"), author, order, runAlways, systemVersion, failFast, changeSetMethod, rollbackMethod, system);
  }
  
  @Override
  public ChangeType getType() {
    return BEFORE_EXECUTION;
  }
  
  @Override
  public boolean isBeforeChangeSets() {
    return true;
  }
}
