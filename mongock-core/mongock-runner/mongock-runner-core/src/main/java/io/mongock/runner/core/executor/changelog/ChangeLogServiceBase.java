package io.mongock.runner.core.executor.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import io.mongock.runner.core.annotation.AnnotationProcessor;
import io.mongock.runner.core.annotation.LegacyAnnotationProcessor;
import io.mongock.api.ChangeLogItem;
import io.mongock.api.ChangeSetItem;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.common.Validable;
import io.mongock.utils.CollectionUtils;
import io.mongock.utils.StringUtils;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.reflections.Reflections;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;


/**
 * Utilities to deal with reflections and annotations
 *
 * @since 27/07/2014
 */
public abstract class ChangeLogServiceBase<CHANGELOG extends ChangeLogItem<CHANGESET>, CHANGESET extends ChangeSetItem> implements Validable {


  private final LegacyAnnotationProcessor<CHANGESET> legacyAnnotationProcessor;
  private final AnnotationProcessor annotationProcessor;
  protected Function<AnnotatedElement, Boolean> profileFilter;
  private Function<Class<?>, Object> changeLogInstantiator;
  private List<String> changeLogsBasePackageList = Collections.emptyList();
  private List<Class<?>> changeLogsBaseClassList = Collections.emptyList();
  private ArtifactVersion startSystemVersion = new DefaultArtifactVersion("0");
  private ArtifactVersion endSystemVersion = new DefaultArtifactVersion(String.valueOf(Integer.MAX_VALUE));

  public ChangeLogServiceBase(AnnotationProcessor annotationProcessor, LegacyAnnotationProcessor<CHANGESET> legacyAnnotationProcessor) {
    this.legacyAnnotationProcessor = legacyAnnotationProcessor;
    this.annotationProcessor = annotationProcessor;
  }

  protected LegacyAnnotationProcessor<CHANGESET> getLegacyAnnotationProcessor() {
    return legacyAnnotationProcessor;
  }

  protected AnnotationProcessor getAnnotationProcessor() {
    return annotationProcessor;
  }

  protected List<String> getChangeLogsBasePackageList() {
    return changeLogsBasePackageList;
  }

  public void setChangeLogsBasePackageList(List<String> changeLogsBasePackageList) {
    this.changeLogsBasePackageList = changeLogsBasePackageList;
  }

  protected List<Class<?>> getChangeLogsBaseClassList() {
    return changeLogsBaseClassList;
  }

  public void setChangeLogsBaseClassList(List<Class<?>> changeLogsBaseClassList) {
    this.changeLogsBaseClassList = changeLogsBaseClassList;
  }

  protected ArtifactVersion getStartSystemVersion() {
    return startSystemVersion;
  }

  public void setStartSystemVersion(String startSystemVersion) {
    this.startSystemVersion = new DefaultArtifactVersion(startSystemVersion);
  }

  protected ArtifactVersion getEndSystemVersion() {
    return endSystemVersion;
  }

  public void setEndSystemVersion(String endSystemVersion) {
    this.endSystemVersion = new DefaultArtifactVersion(endSystemVersion);
  }

  protected Function<AnnotatedElement, Boolean> getProfileFilter() {
    return profileFilter;
  }

  public void setProfileFilter(Function<AnnotatedElement, Boolean> profileFilter) {
    this.profileFilter = profileFilter;
  }

  protected Optional<Function<Class<?>, Object>> getChangeLogInstantiator() {
    return Optional.ofNullable(changeLogInstantiator);
  }

  @Override
  public void runValidation() throws MongockException {
    if (
        (CollectionUtils.isNullEmpty(changeLogsBasePackageList) || !changeLogsBasePackageList.stream().allMatch(StringUtils::hasText))
            && CollectionUtils.isNullEmpty(changeLogsBaseClassList)) {
      throw new MongockException("Scan package for changeLogs is not set: use appropriate setter");
    }
  }

  public SortedSet<CHANGELOG> fetchChangeLogs() {
    return mergeChangeLogClassesAndPackages()
        .stream()
        .filter(changeLogClass -> this.profileFilter != null ? this.profileFilter.apply(changeLogClass) : true)
        .map(this::buildChangeLogObject)
        .collect(Collectors.toCollection(() -> new TreeSet<>(new ChangeLogComparator())));
  }

  private Set<Class<?>> mergeChangeLogClassesAndPackages() {
    //the following check is needed because reflection library will bring the entire classpath in case the changeLogsBasePackageList is empty
    final Stream<Class<?>> scannedPackageStream = changeLogsBasePackageList != null && !changeLogsBasePackageList.isEmpty()
        ? Stream.concat(
          new Reflections(changeLogsBasePackageList).getTypesAnnotatedWith(ChangeLog.class).stream(),
          new Reflections(changeLogsBasePackageList).getTypesAnnotatedWith(ChangeUnit.class).stream())
        : Stream.empty();
    return Stream.concat(changeLogsBaseClassList.stream(), scannedPackageStream).collect(Collectors.toSet());
  }

  protected List<CHANGESET> fetchChangeSetMethodsSorted(Class<?> type) throws MongockException {
    List<CHANGESET> changeSets = getChangeSetWithCompanionMethods(asList(type.getDeclaredMethods()));
    changeSets.sort(new ChangeSetComparator());
    return changeSets;
  }


  private List<CHANGESET> getChangeSetWithCompanionMethods(List<Method> allMethods) throws MongockException {
    //list to be returned
    List<CHANGESET> result = new ArrayList<>();
    Set<String> changeSetIdsAlreadyProcessed = new HashSet<>();
    allMethods.stream().filter(legacyAnnotationProcessor::isMethodAnnotatedAsChange).collect(Collectors.toList())
        .forEach(changeSetMethod -> {
          String changeSetId = legacyAnnotationProcessor.getId(changeSetMethod);
          CHANGESET changeSetItem = legacyAnnotationProcessor.getChangePerformerItem(changeSetMethod, null);
          checkChangeSetDuplication(changeSetIdsAlreadyProcessed, changeSetId);
          changeSetIdsAlreadyProcessed.add(changeSetId);
          if (isChangeSetWithinSystemVersionRange(changeSetItem)) {
            result.add(changeSetItem);
          }
        });
    return result;
  }

  //todo Create a SystemVersionChecker
  private boolean isChangeSetWithinSystemVersionRange(CHANGESET changeSetAnn) {
    boolean isWithinVersion = false;
    String versionString = changeSetAnn.getSystemVersion();
    ArtifactVersion version = new DefaultArtifactVersion(versionString);
    if (version.compareTo(startSystemVersion) >= 0 && version.compareTo(endSystemVersion) <= 0) {
      isWithinVersion = true;
    }
    return isWithinVersion;
  }

  private CHANGELOG buildChangeLogObject(Class<?> changeLogClass) {
    try {
      return isLegacyAnnotation(changeLogClass)
          ? buildChangeLogInstanceFromLegacy(changeLogClass)
          : buildChangeLogInstance(changeLogClass);
    } catch (MongockException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new MongockException(ex);
    }
  }

  private boolean isLegacyAnnotation(Class<?> changeLogClass) {
    return !changeLogClass.isAnnotationPresent(ChangeUnit.class);
  }

  protected abstract CHANGELOG buildChangeLogInstance(Class<?> changeLogClass) throws MongockException;

  protected abstract CHANGELOG buildChangeLogInstanceFromLegacy(Class<?> changeLogClass) throws MongockException;

  private class ChangeSetComparator implements Comparator<CHANGESET>, Serializable {
    private static final long serialVersionUID = -854690868262484102L;

    @Override
    public int compare(CHANGESET c1, CHANGESET c2) {
      return c1.getOrder().compareTo(c2.getOrder());
    }
  }

  private class ChangeLogComparator implements Comparator<CHANGELOG>, Serializable {
    private static final long serialVersionUID = -358162121872177974L;



    /**
     * if order1 and order2 are not null and different, it return their compare. If one of then is null, the other is first.
     * If both are null or equals, they are compare bby their names
     */
    @Override
    public int compare(CHANGELOG changeLog1, CHANGELOG changeLog2) {
      String val1 = changeLog1.getOrder();
      String val2 = changeLog2.getOrder();

      if (StringUtils.hasText(val1) && StringUtils.hasText(val2) && !val1.equals(val2)) {
        return val1.compareTo(val2);
      } else if (StringUtils.hasText(val1) && !StringUtils.hasText(val2)) {
        return -1;
      } else if (StringUtils.hasText(val2) && !StringUtils.hasText(val1)) {
        return 1;
      } else {
        return changeLog1.getType().getCanonicalName().compareTo(changeLog2.getType().getCanonicalName());
      }

    }
  }


  private void checkChangeSetDuplication(Set<String> changeSetIdsAlreadyProcessed, String changeSetId) {
    if (changeSetIdsAlreadyProcessed.contains(changeSetId)) {
      throw new MongockException(String.format("Duplicated changeset id found: '%s'", changeSetId));
    }
  }

  private void checkRollbackMatchesChangeSet(Set<String> changeSetIds, Method method, String rollbackId) {
    if (!changeSetIds.contains(rollbackId)) {
      throw new MongockException(String.format(
          "Rollback method[%s] in class[%s] with id[%s] doesn't match any changeSet",
          method.getName(),
          method.getDeclaringClass().getSimpleName(),
          rollbackId));
    }
  }

  private void checkRollbackDuplication(Set<String> rollbacksAlreadyProcessed, String rollbackId) {
    if (rollbacksAlreadyProcessed.contains(rollbackId)) {
      throw new MongockException(String.format(
          "Multiple rollbacks matching the same changeSetId[%s]. Only one rollback allowed per changeSet",
          rollbackId
      ));
    }
  }

  protected List<CHANGESET> fetchListOfChangeSetsFromClass(Class<?> type) {
    return getAllChanges(type)
        .filter(changeSetItem -> getLegacyAnnotationProcessor().isChangeSet(changeSetItem.getMethod()))
        .collect(Collectors.toList());
  }


  private Stream<CHANGESET> getAllChanges(Class<?> type) {
    return fetchChangeSetMethodsSorted(type)
        .stream()
        .filter(changeSet -> this.profileFilter != null ? this.profileFilter.apply(changeSet.getMethod()) : true);
  }
}
