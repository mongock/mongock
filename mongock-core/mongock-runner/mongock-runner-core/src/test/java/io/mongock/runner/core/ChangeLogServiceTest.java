package io.mongock.runner.core;


import io.mongock.api.exception.MongockException;
import io.mongock.runner.core.changelogs.comparator.Comparator1ChangeLog;
import io.mongock.runner.core.changelogs.comparator.Comparator2ChangeLog;
import io.mongock.runner.core.changelogs.forchangeservice.annotated.AnnotatedChangeLog;
import io.mongock.runner.core.changelogs.forchangeservice.basichangeloginterface.BasicChangeLogUniqueInPackage;
import io.mongock.runner.core.changelogs.forchangeservice.changeloginterface.AdvanceChangeLogUniqueInPackage;
import io.mongock.runner.core.changelogs.instantiator.bad.BadChangeLogCustomConstructor;
import io.mongock.runner.core.changelogs.instantiator.good.ChangeLogCustomConstructor;
import io.mongock.runner.core.changelogs.multipackage.ChangeLogNoPackage;
import io.mongock.runner.core.changelogs.multipackage.package1.ChangeLogMultiPackage1;
import io.mongock.runner.core.changelogs.multipackage.package2.ChangeLogMultiPackage2;
import io.mongock.runner.core.changelogs.system.SystemChangeUnit;
import io.mongock.runner.core.changelogs.systemversion.ChangeLogSystemVersion;
import io.mongock.runner.core.changelogs.systemversion.ChangeUnitSystemVersion1;
import io.mongock.runner.core.changelogs.systemversion.ChangeUnitSystemVersion3;
import io.mongock.runner.core.changelogs.systemversion.ChangeUnitSystemVersion6;
import io.mongock.runner.core.changelogs.test1.ChangeLogSuccess11;
import io.mongock.runner.core.changelogs.test1.ChangeLogSuccess12;
import io.mongock.runner.core.changelogs.withDuplications.changesetsduplicated.ChangeLogDuplicated1;
import io.mongock.runner.core.changelogs.withRollback.BasicChangeLogWithRollback;
import io.mongock.runner.core.changelogs.with_author_empty.ChangeLogWithAuthorEmpty;
import io.mongock.runner.core.changelogs.with_author_empty.ChangeUnitWithAuthorEmpty;
import io.mongock.runner.core.changelogs.withnoannotations.ChangeLogNormal;
import io.mongock.runner.core.executor.changelog.ChangeLogService;
import io.mongock.runner.core.internal.ChangeLogItem;
import io.mongock.runner.core.internal.ChangeSetItem;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChangeLogServiceTest {

  @Test
  public void shouldSucceed_WhenValidate_ifParametersAreOk() {
    new ChangeLogService(Collections.singletonList("fake.changelog.package"), Collections.emptyList(), "0", "999")
        .runValidation();
  }

  @Test
  public void shouldFail_WhenValidate_ifParametersEmpty() {
    assertThrows(MongockException.class, () -> 
            new ChangeLogService(Collections.emptyList(), Collections.emptyList(), "0", "999")
                .runValidation()
    );
  }

  private static Function<Class<?>, Object> mockInjector() {
    return (type) -> {
      try {
        if (type == ChangeLogCustomConstructor.class) {
          return type.getConstructor(String.class, int.class).newInstance("string", 10);
        } else if (type == BadChangeLogCustomConstructor.class) {
          throw new RuntimeException("Cannot instantiate BadChangeLogCustomConstructor");
        } else {
          return type.getConstructor().newInstance();
        }
      } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
        throw new MongockException(e);
      }
    };
  }

  @Test
  public void shouldThrowException_WhenValidatingChangeLogs_IfDuplicatedChangeSets() {

    MongockException ex = assertThrows(MongockException.class, () ->
        new ArrayList<>(new ChangeLogService(
            Collections.singletonList(ChangeLogDuplicated1.class.getPackage().getName()),
            Collections.emptyList(),
            "0",
            "9999"
        ).fetchChangeLogs())
    );

    assertTrue(ex.getMessage().contains("duplicated"));

  }

  @Test
  public void shouldOnlyRunAnnotatedClassesAndMethods() {
    List<ChangeLogItem> changeLogItemList = new ArrayList<>(new ChangeLogService(
        Collections.singletonList(ChangeLogNormal.class.getPackage().getName()),
        Collections.emptyList(),
        "0",
        "9999"
    ).fetchChangeLogs());

    assertEquals(3, changeLogItemList.size());

    // Normal
    ChangeLogItem changeLogItem = changeLogItemList.get(0);
    assertEquals(1, changeLogItem.getChangeSetItems().size());
    assertEquals("changeSet_0", changeLogItem.getChangeSetItems().get(0).getId());

    // With both, one annotated changeSet and a method with no annotation
    changeLogItem = changeLogItemList.get(1);
    assertEquals(1, changeLogItem.getChangeSetItems().size());
    assertEquals("changeSet_1", changeLogItem.getChangeSetItems().get(0).getId());

    // ChangeLog annotated class, with no annotated changeSet
    changeLogItem = changeLogItemList.get(2);
    assertEquals(0, changeLogItem.getChangeSetItems().size());
  }


  @Test
  public void shouldNotThrowException_whenAuthorIsEmpty_IfOldChangeLog() {
    List<ChangeLogItem> changeLogItemList = new ArrayList<>(new ChangeLogService(
        Collections.emptyList(),
        Collections.singletonList(ChangeLogWithAuthorEmpty.class),
        "0",
        "9999"
    ).fetchChangeLogs());
    assertEquals(1, changeLogItemList.size());
    ChangeSetItem changeSetItem = changeLogItemList.get(0).getChangeSetItems().get(0);
    assertEquals("changeSet_0", changeSetItem.getId());

  }


  @Test
  public void shouldThrowException_whenAuthorIsEmpty_IfChangeUnit() {
    MongockException ex = assertThrows(MongockException.class, () ->
        new ChangeLogService(
            Collections.emptyList(),
            Collections.singletonList(ChangeUnitWithAuthorEmpty.class),
            "0",
            "9999"
        ).fetchChangeLogs());
    assertEquals("changeUnit[" + ChangeUnitWithAuthorEmpty.class.getName() + "] author cannot be null or empty.", ex.getMessage());
  }

  @Test
  public void shouldNotThrowException_whenChangeUnitAuthorIsEmpty_IfSetDefaultAuthor() {
    ChangeLogService changeLogService = new ChangeLogService(
        Collections.emptyList(),
        Collections.singletonList(ChangeUnitWithAuthorEmpty.class),
        "0",
        "9999"
    );
    changeLogService.setDefaultMigrationAuthor("default_author");
    List<ChangeLogItem> changeLogItemList = new ArrayList<>(changeLogService.fetchChangeLogs());
    assertEquals(1, changeLogItemList.size());
    ChangeSetItem changeSetItem = changeLogItemList.get(0).getChangeSetItems().get(0);
    assertEquals("default_author", changeSetItem.getAuthor());
  }


  @Test
  public void shouldReturnRightChangeLogItems_whenFetchingLogs_ifPackageIsRight() {
    List<ChangeLogItem> changeLogItemList = new ArrayList<>(new ChangeLogService(
        Collections.singletonList(ChangeLogSuccess11.class.getPackage().getName()),
        Collections.emptyList(),
        "0",
        "9999"
    ).fetchChangeLogs());

    assertEquals(2, changeLogItemList.size());
    ChangeLogItem changeLogItem11 = changeLogItemList.get(0);
    validateChangeLog(changeLogItem11, 1);
    assertEquals(1, changeLogItem11.getChangeSetItems().size());
    changeLogItem11.getChangeSetItems().forEach(changeSetItem -> validateChangeSet(changeSetItem, 1));

    ChangeLogItem changeLogItem12 = changeLogItemList.get(1);
    validateChangeLog(changeLogItem12, 2);
    assertEquals(1, changeLogItem12.getChangeSetItems().size());
    changeLogItem12.getChangeSetItems().forEach(changeSetItem -> validateChangeSet(changeSetItem, 2));
  }

  @Test
  public void shouldReturnOnlyChangeSetsWithinSystemVersionRangeInclusive() {
    List<? extends ChangeSetItem> allChangeSets = getChangeSetItems("0", "9", ChangeLogSystemVersion.class);
    assertEquals(6, allChangeSets.size());

    List<? extends ChangeSetItem> systemVersionedChangeSets = getChangeSetItems("2", "4", ChangeLogSystemVersion.class);
    assertEquals(3, systemVersionedChangeSets.size());
    systemVersionedChangeSets.stream()
        .map(ChangeSetItem::getId)
        .collect(Collectors.toList())
        .containsAll(Arrays.asList("ChangeSet_2", "ChangeSet_3.0", "ChangeSet_4"));

    systemVersionedChangeSets = getChangeSetItems("3", "4", ChangeLogSystemVersion.class);
    assertEquals(2, systemVersionedChangeSets.size());
    systemVersionedChangeSets.stream()
        .map(ChangeSetItem::getId)
        .collect(Collectors.toList())
        .containsAll(Arrays.asList("ChangeSet_3.0", "ChangeSet_4"));

    systemVersionedChangeSets = getChangeSetItems("3", "2018", ChangeLogSystemVersion.class);
    assertEquals(5, systemVersionedChangeSets.size());
    systemVersionedChangeSets.stream()
        .map(ChangeSetItem::getId)
        .collect(Collectors.toList())
        .containsAll(Arrays.asList("ChangeSet_3.0", "ChangeSet_4", "ChangeSet_5", "ChangeSet_6", "ChangeSet_2018"));


    systemVersionedChangeSets = getChangeSetItems("2", "3", ChangeUnitSystemVersion3.class, ChangeUnitSystemVersion6.class, ChangeUnitSystemVersion1.class);
    assertEquals(1, systemVersionedChangeSets.size());
    systemVersionedChangeSets.stream()
        .map(ChangeSetItem::getId)
        .collect(Collectors.toList())
        .containsAll(Collections.singletonList("change-unit-system-version-3"));


  }

  private List<? extends ChangeSetItem> getChangeSetItems(String startingVersion, String endingVersion, Class<?>... changeClasses) {
    return new ArrayList<>(new ChangeLogService(
        Collections.emptyList(),
        Arrays.asList(changeClasses),
        startingVersion,
        endingVersion)
        .fetchChangeLogs())
        .stream()
        .map(ChangeLogItem::getChangeSetItems)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }


  @Test
  public void shouldReturnChangeSetsFromMultiplePackagesAndKeepsOrder() {
    List<ChangeLogItem> changeLogItemList = new ArrayList<>(new ChangeLogService(
        Arrays.asList(ChangeLogMultiPackage1.class.getPackage().getName(), ChangeLogMultiPackage2.class.getPackage().getName()),
        Collections.emptyList(),
        "0",
        "9999"
    ).fetchChangeLogs());

    assertEquals(2, changeLogItemList.size());
    ChangeLogItem changeLogPackage = changeLogItemList.get(0);
    assertEquals(1, changeLogPackage.getChangeSetItems().size());
    ChangeSetItem changeSet = changeLogPackage.getChangeSetItems().get(0);
    assertEquals("changeset_package1", changeSet.getId());
    assertEquals("changeSetPackage1", changeSet.getMethod().getName());


    changeLogPackage = changeLogItemList.get(1);
    assertEquals(1, changeLogPackage.getChangeSetItems().size());
    changeSet = changeLogPackage.getChangeSetItems().get(0);
    assertEquals("changeset_package2", changeSet.getId());
    assertEquals("changeSetPackage2", changeSet.getMethod().getName());

  }

  @Test
  public void shouldReturnSystemChangelogsFirst() {
    List<ChangeLogItem> changeLogItemList = new ArrayList<>(new ChangeLogService(
        Collections.singletonList(SystemChangeUnit.class.getPackage().getName()),
        Collections.emptyList(),
        "0",
        "9999"
    ).fetchChangeLogs());

    assertEquals(2, changeLogItemList.size());
    assertEquals("system-change-unit", changeLogItemList.get(0).getChangeSetItems().get(0).getId());
    assertEquals("new-change-unit", changeLogItemList.get(1).getChangeSetItems().get(0).getId());
  }

  @Test
  public void shouldReturnChangeSetsFromMultiplePackagesAndIsolatedClassesAndKeepsOrder() {
    List<String> changeLogsBasePackageList = Arrays.asList(
        ChangeLogMultiPackage1.class.getPackage().getName(),
        ChangeLogMultiPackage2.class.getPackage().getName(),
        ChangeLogNoPackage.class.getName());
    List<ChangeLogItem> changeLogItemList = new ArrayList<>(new ChangeLogService(
        changeLogsBasePackageList,
        Collections.emptyList(),
        "0",
        "9999"
    ).fetchChangeLogs());

    //package 1
    assertEquals(3, changeLogItemList.size());
    ChangeLogItem changeLogPackage = changeLogItemList.get(0);
    assertEquals(1, changeLogPackage.getChangeSetItems().size());
    ChangeSetItem changeSet1 = changeLogPackage.getChangeSetItems().get(0);
    assertEquals("changeset_package1", changeSet1.getId());
    assertEquals("changeSetPackage1", changeSet1.getMethod().getName());

    //isolated class
    changeLogPackage = changeLogItemList.get(1);
    assertEquals(2, changeLogPackage.getChangeSetItems().size());
    ChangeSetItem changeSet2 = changeLogPackage.getChangeSetItems().get(0);
    ChangeSetItem changeSet3 = changeLogPackage.getChangeSetItems().get(1);


    assertEquals("no_package", changeSet2.getId());
    assertEquals("noPackage", changeSet2.getMethod().getName());
    assertEquals("no_package_2", changeSet3.getId());
    assertEquals("noPackage2", changeSet3.getMethod().getName());


    //package 2
    changeLogPackage = changeLogItemList.get(2);
    assertEquals(1, changeLogPackage.getChangeSetItems().size());
    ChangeSetItem changeSet4 = changeLogPackage.getChangeSetItems().get(0);
    assertEquals("changeset_package2", changeSet4.getId());
    assertEquals("changeSetPackage2", changeSet4.getMethod().getName());
  }

  private void validateChangeLog(ChangeLogItem changeLogItem, int number) {
    assertEquals(String.valueOf(number), changeLogItem.getOrder());
  }

  private void validateChangeSet(ChangeSetItem changeSetItem, int number) {
    assertEquals("testUser1" + number, changeSetItem.getAuthor());
    assertEquals("ChangeSet_12" + number, changeSetItem.getId());
    assertEquals(String.valueOf(number), changeSetItem.getOrder());
    assertTrue(changeSetItem.isRunAlways());
    assertEquals(String.valueOf(number), changeSetItem.getSystemVersion());
  }


  @Test
  public void shouldReturnChangelogs() {
    List<ChangeLogItem> changeLogItemList = new ArrayList<>(new ChangeLogService(
        Arrays.asList(Comparator1ChangeLog.class.getPackage().getName()),
        Collections.emptyList(),
        "0",
        "9999"
    ).fetchChangeLogs());

    assertEquals(2, changeLogItemList.size());
    changeLogItemList.forEach(changeLogItem -> assertTrue(changeLogItem.getType() == Comparator1ChangeLog.class
        || changeLogItem.getType() == Comparator2ChangeLog.class));

  }


  @Test
  public void shouldNotDuplicateWhenAddingSingleClassIfTwice() {

    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBaseClassList(Arrays.asList(ChangeLogSuccess11.class, ChangeLogSuccess11.class));

    List<ChangeLogItem> changeLogs = new ArrayList<>(changeLogService.fetchChangeLogs());

    assertEquals(1, changeLogs.size());

  }


  @Test
  public void shouldAddClassAndPackage() {


    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBaseClassList(Collections.singletonList(ChangeLogSuccess11.class));
    changeLogService.setChangeLogsBasePackageList(Collections.singletonList(ChangeLogSuccess11.class.getPackage().getName()));

    List<ChangeLogItem> changeLogItemsList = new ArrayList<>(changeLogService.fetchChangeLogs());

    assertEquals(2, changeLogItemsList.size());

    ChangeLogItem changeLogItem = changeLogItemsList.get(0);
    assertEquals(ChangeLogSuccess11.class, changeLogItem.getType());
    assertEquals("1", changeLogItem.getOrder());

    ChangeLogItem changeLogItem2 = changeLogItemsList.get(1);
    assertEquals(ChangeLogSuccess12.class, changeLogItem2.getType());
    assertEquals("2", changeLogItem2.getOrder());
  }

  @Test
  public void shouldAddSingleClass() {


    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBaseClassList(Collections.singletonList(ChangeLogSuccess11.class));

    List<ChangeLogItem> changeLogs = new ArrayList<>(changeLogService.fetchChangeLogs());


    ChangeLogItem changeLogItem = changeLogs.get(0);
    assertEquals(ChangeLogSuccess11.class, changeLogItem.getType());
    assertEquals("1", changeLogItem.getOrder());

    ChangeSetItem changeSetItem = changeLogItem.getChangeSetItems().get(0);
    assertEquals("ChangeSet_121", changeSetItem.getId());
    assertEquals("testUser11", changeSetItem.getAuthor());
    assertEquals("1", changeSetItem.getOrder());
    assertTrue(changeSetItem.isRunAlways());
    assertEquals("1", changeSetItem.getSystemVersion());
    assertEquals("method_111", changeSetItem.getMethod().getName());
    assertTrue(changeSetItem.isFailFast());

  }

  @Test
  public void shouldReturnBasicChangeSetWithRollBack() {
    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBaseClassList(Collections.singletonList(BasicChangeLogWithRollback.class));

    List<ChangeLogItem> changeLogs = new ArrayList<>(changeLogService.fetchChangeLogs());

    assertEquals(1, changeLogs.size());
    ChangeSetItem changeSetItem = changeLogs.get(0).getChangeSetItems().get(0);

    assertEquals("changeset_with_rollback_1", changeSetItem.getId());
    assertEquals("changeSet", changeSetItem.getMethod().getName());
    assertEquals("rollback", changeSetItem.getRollbackMethod().get().getName());
    assertEquals("mongock_test", changeSetItem.getAuthor());
    assertEquals("1", changeSetItem.getOrder());
    assertEquals("1", changeSetItem.getSystemVersion());
    assertTrue(changeSetItem.isFailFast());
  }

  @Test
  public void shouldReturnAdvancedChangeSetWithRollBackWhenClassIsPassed() {
    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBaseClassList(Collections.singletonList(AdvanceChangeLogUniqueInPackage.class));

    List<ChangeLogItem> changeLogs = new ArrayList<>(changeLogService.fetchChangeLogs());

    assertEquals(1, changeLogs.size());
    ChangeLogItem changeLogItem = changeLogs.get(0);

    assertEquals(1, changeLogItem.getChangeSetItems().size());
    ChangeSetItem changeSetItem = changeLogItem.getChangeSetItems().get(0);

    assertEquals(1, changeLogItem.getBeforeItems().size());
    ChangeSetItem beforeItem = changeLogItem.getBeforeItems().get(0);


    //changeset
    assertEquals(AdvanceChangeLogUniqueInPackage.class.getSimpleName(), changeSetItem.getId());
    assertEquals("changeSet", changeSetItem.getMethod().getName());
    assertEquals("rollback", changeSetItem.getRollbackMethod().get().getName());
    assertEquals("mongock_test", changeSetItem.getAuthor());
    assertEquals("1", changeSetItem.getOrder());
    assertEquals("1", changeSetItem.getSystemVersion());
    assertTrue(changeSetItem.isFailFast());
    assertFalse(changeSetItem.isBeforeChangeSets());
    //before
    assertEquals(AdvanceChangeLogUniqueInPackage.class.getSimpleName() + "_before", beforeItem.getId());
    assertEquals("before", beforeItem.getMethod().getName());
    assertEquals("rollbackBefore", beforeItem.getRollbackMethod().get().getName());
    assertEquals("mongock_test", beforeItem.getAuthor());
    assertEquals("1", beforeItem.getOrder());
    assertEquals("1", beforeItem.getSystemVersion());
    assertTrue(beforeItem.isFailFast());
    assertTrue(beforeItem.isBeforeChangeSets());
  }

  @Test
  public void shouldReturnAdvancedChangeSetWithRollBackWhenPackageIsPassed() {
    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBasePackageList(Collections.singletonList(AdvanceChangeLogUniqueInPackage.class.getPackage().getName()));

    List<ChangeLogItem> changeLogs = new ArrayList<>(changeLogService.fetchChangeLogs());

    assertEquals(1, changeLogs.size());
    ChangeLogItem changeLogItem = changeLogs.get(0);

    assertEquals(1, changeLogItem.getChangeSetItems().size());
    ChangeSetItem changeSetItem = changeLogItem.getChangeSetItems().get(0);

    assertEquals(1, changeLogItem.getBeforeItems().size());
    ChangeSetItem beforeItem = changeLogItem.getBeforeItems().get(0);


    //changeset
    assertEquals(AdvanceChangeLogUniqueInPackage.class.getSimpleName(), changeSetItem.getId());
    assertEquals("changeSet", changeSetItem.getMethod().getName());
    assertEquals("rollback", changeSetItem.getRollbackMethod().get().getName());
    assertEquals("mongock_test", changeSetItem.getAuthor());
    assertEquals("1", changeSetItem.getOrder());
    assertEquals("1", changeSetItem.getSystemVersion());
    assertTrue(changeSetItem.isFailFast());
    assertFalse(changeSetItem.isBeforeChangeSets());
    //before
    assertEquals(AdvanceChangeLogUniqueInPackage.class.getSimpleName() + "_before", beforeItem.getId());
    assertEquals("before", beforeItem.getMethod().getName());
    assertEquals("rollbackBefore", beforeItem.getRollbackMethod().get().getName());
    assertEquals("mongock_test", beforeItem.getAuthor());
    assertEquals("1", beforeItem.getOrder());
    assertEquals("1", beforeItem.getSystemVersion());
    assertTrue(beforeItem.isFailFast());
    assertTrue(beforeItem.isBeforeChangeSets());
  }

  @Test
  public void shouldReturnBasicChangeSetWithRollBackWhenClassIsPassed() {
    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBaseClassList(Collections.singletonList(BasicChangeLogUniqueInPackage.class));

    List<ChangeLogItem> changeLogs = new ArrayList<>(changeLogService.fetchChangeLogs());

    assertEquals(1, changeLogs.size());
    ChangeLogItem changeLogItem = changeLogs.get(0);

    assertEquals(1, changeLogItem.getChangeSetItems().size());
    ChangeSetItem changeSetItem = changeLogItem.getChangeSetItems().get(0);

    assertEquals(0, changeLogItem.getBeforeItems().size());


    //changeset
    assertEquals(BasicChangeLogUniqueInPackage.class.getSimpleName(), changeSetItem.getId());
    assertEquals("changeSet", changeSetItem.getMethod().getName());
    assertEquals("rollback", changeSetItem.getRollbackMethod().get().getName());
    assertEquals("mongock_test", changeSetItem.getAuthor());
    assertEquals("2", changeSetItem.getOrder());
    assertEquals("1", changeSetItem.getSystemVersion());
    assertTrue(changeSetItem.isFailFast());
    assertFalse(changeSetItem.isBeforeChangeSets());
    //before

    assertEquals(0, changeLogItem.getBeforeItems().size());
  }

  @Test
  public void shouldReturnBasicChangeSetWithRollBackWhenPackageIsPassed() {
    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBasePackageList(Collections.singletonList(BasicChangeLogUniqueInPackage.class.getPackage().getName()));

    List<ChangeLogItem> changeLogs = new ArrayList<>(changeLogService.fetchChangeLogs());

    assertEquals(1, changeLogs.size());
    ChangeLogItem changeLogItem = changeLogs.get(0);

    assertEquals(1, changeLogItem.getChangeSetItems().size());
    ChangeSetItem changeSetItem = changeLogItem.getChangeSetItems().get(0);

    assertEquals(0, changeLogItem.getBeforeItems().size());


    //changeset
    assertEquals(BasicChangeLogUniqueInPackage.class.getSimpleName(), changeSetItem.getId());
    assertEquals("changeSet", changeSetItem.getMethod().getName());
    assertEquals("rollback", changeSetItem.getRollbackMethod().get().getName());
    assertEquals("mongock_test", changeSetItem.getAuthor());
    assertEquals("2", changeSetItem.getOrder());
    assertEquals("1", changeSetItem.getSystemVersion());
    assertTrue(changeSetItem.isFailFast());
    assertFalse(changeSetItem.isBeforeChangeSets());
    //before

    assertEquals(0, changeLogItem.getBeforeItems().size());
  }


  @Test
  public void shouldReturnAnnotatedChangeSetWhenClassIsPassed() {
    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBaseClassList(Collections.singletonList(AnnotatedChangeLog.class));

    List<ChangeLogItem> changeLogs = new ArrayList<>(changeLogService.fetchChangeLogs());

    assertEquals(1, changeLogs.size());
    ChangeLogItem changeLogItem = changeLogs.get(0);

    assertEquals(1, changeLogItem.getChangeSetItems().size());
    ChangeSetItem changeSetItem = changeLogItem.getChangeSetItems().get(0);

    assertEquals(0, changeLogItem.getBeforeItems().size());


    //changeset
    assertEquals(AnnotatedChangeLog.class.getSimpleName(), changeSetItem.getId());
    assertEquals("changeSet", changeSetItem.getMethod().getName());
    assertFalse(changeSetItem.getRollbackMethod().isPresent());
    assertEquals("mongock_test", changeSetItem.getAuthor());
    assertEquals("3", changeLogItem.getOrder());
    assertEquals("1", changeSetItem.getOrder());
    assertEquals("1", changeSetItem.getSystemVersion());
    assertTrue(changeSetItem.isFailFast());
    assertFalse(changeSetItem.isBeforeChangeSets());
    //before

    assertEquals(0, changeLogItem.getBeforeItems().size());
  }

  @Test
  public void shouldReturnAnnotatedChangeSetWhenPackageIsPassed() {
    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBasePackageList(Collections.singletonList(AnnotatedChangeLog.class.getPackage().getName()));

    List<ChangeLogItem> changeLogs = new ArrayList<>(changeLogService.fetchChangeLogs());

    assertEquals(1, changeLogs.size());
    ChangeLogItem changeLogItem = changeLogs.get(0);

    assertEquals(1, changeLogItem.getChangeSetItems().size());
    ChangeSetItem changeSetItem = changeLogItem.getChangeSetItems().get(0);

    assertEquals(0, changeLogItem.getBeforeItems().size());


    //changeset
    assertEquals(AnnotatedChangeLog.class.getSimpleName(), changeSetItem.getId());
    assertEquals("changeSet", changeSetItem.getMethod().getName());
    assertFalse(changeSetItem.getRollbackMethod().isPresent());
    assertEquals("mongock_test", changeSetItem.getAuthor());
    assertEquals("3", changeLogItem.getOrder());
    assertEquals("1", changeSetItem.getOrder());
    assertEquals("1", changeSetItem.getSystemVersion());
    assertTrue(changeSetItem.isFailFast());
    assertFalse(changeSetItem.isBeforeChangeSets());
    //before
    assertEquals(0, changeLogItem.getBeforeItems().size());
  }


  @Test
  public void shouldReturnAllChangeSetsWhenParentPackageIsPassed() {
    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBasePackageList(Collections.singletonList("io.mongock.runner.core.changelogs.forchangeservice"));

    List<ChangeLogItem> changeLogs = new ArrayList<>(changeLogService.fetchChangeLogs());

    assertEquals(3, changeLogs.size());


    //ADVANCE CHANGELOG
    ChangeLogItem advanceChangeLogItem = changeLogs.get(0);
    assertEquals(1, advanceChangeLogItem.getChangeSetItems().size());
    ChangeSetItem advanceChangeSetItem = advanceChangeLogItem.getChangeSetItems().get(0);
    assertEquals(1, advanceChangeLogItem.getBeforeItems().size());
    ChangeSetItem advanceBeforeItem = advanceChangeLogItem.getBeforeItems().get(0);
    assertEquals(AdvanceChangeLogUniqueInPackage.class.getSimpleName(), advanceChangeSetItem.getId());
    assertEquals("changeSet", advanceChangeSetItem.getMethod().getName());
    assertEquals("rollback", advanceChangeSetItem.getRollbackMethod().get().getName());
    assertEquals("mongock_test", advanceChangeSetItem.getAuthor());
    assertEquals("1", advanceChangeSetItem.getOrder());
    assertEquals("1", advanceChangeSetItem.getSystemVersion());
    assertTrue(advanceChangeSetItem.isFailFast());
    assertFalse(advanceChangeSetItem.isBeforeChangeSets());
    assertEquals(AdvanceChangeLogUniqueInPackage.class.getSimpleName() + "_before", advanceBeforeItem.getId());
    assertEquals("before", advanceBeforeItem.getMethod().getName());
    assertEquals("rollbackBefore", advanceBeforeItem.getRollbackMethod().get().getName());
    assertEquals("mongock_test", advanceBeforeItem.getAuthor());
    assertEquals("1", advanceBeforeItem.getOrder());
    assertEquals("1", advanceBeforeItem.getSystemVersion());
    assertTrue(advanceBeforeItem.isFailFast());
    assertTrue(advanceBeforeItem.isBeforeChangeSets());

    //BASIC CHANGELOG
    ChangeLogItem basicChangeLogItem = changeLogs.get(1);
    assertEquals(1, basicChangeLogItem.getChangeSetItems().size());
    ChangeSetItem basicChangeSetItem = basicChangeLogItem.getChangeSetItems().get(0);
    assertEquals(0, basicChangeLogItem.getBeforeItems().size());
    assertEquals(BasicChangeLogUniqueInPackage.class.getSimpleName(), basicChangeSetItem.getId());
    assertEquals("changeSet", basicChangeSetItem.getMethod().getName());
    assertEquals("rollback", basicChangeSetItem.getRollbackMethod().get().getName());
    assertEquals("mongock_test", basicChangeSetItem.getAuthor());
    assertEquals("2", basicChangeSetItem.getOrder());
    assertEquals("1", basicChangeSetItem.getSystemVersion());
    assertTrue(basicChangeSetItem.isFailFast());
    assertFalse(basicChangeSetItem.isBeforeChangeSets());
    assertEquals(0, basicChangeLogItem.getBeforeItems().size());

    //ANNOTATED CHANGELOG
    ChangeLogItem annotatedChangeLogItem = changeLogs.get(2);
    assertEquals(1, annotatedChangeLogItem.getChangeSetItems().size());
    ChangeSetItem annotatedChangeSetItem = annotatedChangeLogItem.getChangeSetItems().get(0);
    assertEquals(0, annotatedChangeLogItem.getBeforeItems().size());
    assertEquals(AnnotatedChangeLog.class.getSimpleName(), annotatedChangeSetItem.getId());
    assertEquals("changeSet", annotatedChangeSetItem.getMethod().getName());
    assertFalse(annotatedChangeSetItem.getRollbackMethod().isPresent());
    assertEquals("mongock_test", annotatedChangeSetItem.getAuthor());
    assertEquals("3", annotatedChangeLogItem.getOrder());
    assertEquals("1", annotatedChangeSetItem.getOrder());
    assertEquals("1", annotatedChangeSetItem.getSystemVersion());
    assertTrue(annotatedChangeSetItem.isFailFast());
    assertFalse(annotatedChangeSetItem.isBeforeChangeSets());
    assertEquals(0, annotatedChangeLogItem.getBeforeItems().size());
  }

}
