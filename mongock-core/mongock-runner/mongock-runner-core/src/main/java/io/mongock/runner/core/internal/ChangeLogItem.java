package io.mongock.runner.core.internal;

import io.mongock.api.exception.MongockException;
import io.mongock.utils.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ChangeLogItem {


  private final String id;

  private final Class<?> type;

  private final String order;

  private final boolean failFast;

  private final boolean transactional;

  private final List<ChangeSetItem> changeSetItems;

  private final List<ChangeSetItem> beforeChangeSetsItems;

  private final boolean system;

  public static ChangeLogItem getFromAnnotation(
      Class<?> changeLogClass,
      String baseId,
      String author,
      String order,
      boolean failFast,
      boolean transactional,
      boolean runAlways,
      String systemVersion,
      Method executionMethod,
      Method rollbackExecutionMethod,
      Method beforeMethod,
      Method rollbackBeforeMethod,
      boolean system) {
    if(!StringUtils.hasText(author)) {
      throw new MongockException("changeUnit[" + changeLogClass.getName() + "] author cannot be null or empty.");
    }
    ChangeSetItem changeSet = new ChangeSetItem(
        baseId,
        author,
        order,
        runAlways,
        systemVersion,
        failFast,
        executionMethod,
        rollbackExecutionMethod);
    List<ChangeSetItem> changeSetBeforeList = new ArrayList<>();
    if (beforeMethod != null) {
      changeSetBeforeList.add(new BeforeChangeSetItem(
          baseId,
          author,
          order,
          runAlways,
          systemVersion,
          failFast,
          beforeMethod,
          rollbackBeforeMethod
          ));

    }
    return new ChangeLogItem(
        baseId,
        changeLogClass,
        order,
        failFast,
        transactional,
        Collections.singletonList(changeSet),
        changeSetBeforeList,
        system);

  }

  public static ChangeLogItem getFromLegacy(
      Class<?> type,
      String order,
      boolean failFast,
      List<ChangeSetItem> changeSetElements,
      boolean system) {
    return new ChangeLogItem(type.getName(), type, order, failFast, true,  changeSetElements, Collections.emptyList(), system);
  }
  
  public static ChangeLogItem withId(String id) {
    return new ChangeLogItem(id, null, null, false, false, null, null, false);
  }

  public ChangeLogItem(String id,
                       Class<?> type,
                       String order,
                       boolean failFast,
                       boolean transactional,
                       List<ChangeSetItem> changeSetElements,
                       List<ChangeSetItem> beforeChangeSetsItems,
                       boolean system) {
    if (id == null || id.trim().isEmpty()) {
      throw new MongockException("changeUnit[" + type.getName() + "] id cannot be null or empty.");
    }
    this.id = id;
    this.type = type;
    this.order = order;
    this.failFast = failFast;
    this.transactional = transactional;
    this.changeSetItems = changeSetElements != null ? changeSetElements : new ArrayList<>();
    this.beforeChangeSetsItems = beforeChangeSetsItems != null ? beforeChangeSetsItems : new ArrayList<>();
    this.system = system;
  }

  public String getId() {
    return id;
  }

  public Class<?> getType() {
    return type;
  }

  public String getOrder() {
    return order;
  }

  public boolean isFailFast() {
    return failFast;
  }

  public boolean isTransactional() {
    return transactional;
  }

  public List<ChangeSetItem> getChangeSetItems() {
    return changeSetItems;
  }

  public List<ChangeSetItem> getBeforeItems() {
    return beforeChangeSetsItems;
  }

  
  public List<ChangeSetItem> getAllChangeItems() {
    return Stream.concat(beforeChangeSetsItems.stream(),changeSetItems.stream()).collect(Collectors.toList());
  }

  public boolean isSystem() {
    return system;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangeLogItem that = (ChangeLogItem) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

}
