package io.mongock.api;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.exception.MongockException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.mongock.api.ChangeLogItemType.LEGACY_ANNOTATION;
import static io.mongock.api.ChangeLogItemType.NEW_CHANGE_ANNOTATION;

public class ChangeLogItem<CHANGESET extends ChangeSetItem> {


  private final String id;

  private final Class<?> type;

  private final String order;

  private final boolean failFast;

  private final List<CHANGESET> changeSetItems;

  private final List<CHANGESET> beforeChangeSetsItems;

  public static ChangeLogItem<ChangeSetItem> getFromAnnotation(
      Class<?> changeLogClass,
      String baseId,
      String author,
      String order,
      boolean failFast,
      boolean runAlways,
      String systemVersion,
      Method executionMethod,
      Method rollbackExecutionMethod,
      Method beforeMethod,
      Method rollbackBeforeMethod) {
    ChangeSetItem changeSet = new ChangeSetItem(
        baseId,
        author,
        order,
        runAlways,
        systemVersion,
        failFast,
        executionMethod,
        rollbackExecutionMethod,
        false);
    List<ChangeSetItem> changeSetBeforeList = new ArrayList<>();
    if (beforeMethod != null) {
      changeSetBeforeList.add(new ChangeSetItem(
          String.format("%s_%s", baseId, "before"),
          author,
          order,
          runAlways,
          systemVersion,
          failFast,
          beforeMethod,
          rollbackBeforeMethod,
          true));

    }
    return new ChangeLogItem<>(
        baseId,
        changeLogClass,
        order,
        failFast,
        Collections.singletonList(changeSet),
        changeSetBeforeList);

  }

  public static <C extends ChangeSetItem> ChangeLogItem<C> getFromLegacy(
      Class<?> type,
      String order,
      boolean failFast,
      List<C> changeSetElements) {
    return new ChangeLogItem<>(type.getName(), type, order, failFast, changeSetElements, Collections.emptyList());
  }

  public ChangeLogItem(String id,
                       Class<?> type,
                       String order,
                       boolean failFast,
                       List<CHANGESET> changeSetElements,
                       List<CHANGESET> beforeChangeSetsItems) {
    this.id = id;
    this.type = type;
    this.order = order;
    this.failFast = failFast;
    this.changeSetItems = changeSetElements != null ? changeSetElements : new ArrayList<>();
    this.beforeChangeSetsItems = beforeChangeSetsItems != null ? beforeChangeSetsItems : new ArrayList<>();
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

  public List<CHANGESET> getChangeSetItems() {
    return changeSetItems;
  }

  public List<CHANGESET> getBeforeItems() {
    return beforeChangeSetsItems;
  }

  
  public List<CHANGESET> getAllChangeItems() {
    return Stream.concat(beforeChangeSetsItems.stream(),changeSetItems.stream()).collect(Collectors.toList());
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangeLogItem<?> that = (ChangeLogItem<?>) o;
    return type.equals(that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }
}
