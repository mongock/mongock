package com.github.cloudyrock.mongock;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

import static com.github.cloudyrock.mongock.StringUtils.hasText;
import static java.util.Arrays.asList;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.reflections.Reflections;

//TODO: this can become a Util class, no a service: static methods and name is confusing
/**
 * Utilities to deal with reflections and annotations
 *
 * @since 27/07/2014
 */
class ChangeService {

  private String changeLogsBasePackage;

  private ArtifactVersion startVersion = new DefaultArtifactVersion("0");

  private ArtifactVersion endVersion = new DefaultArtifactVersion(String.valueOf(Integer.MAX_VALUE));

  ChangeService() {
  }

  private static boolean isProfileAnnotationPresent() {
    try {
      Class.forName("org.springframework.context.annotation.Profile");
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  /**
   * <p>Indicates the package to scan changeLogs</p>
   *
   * @param changeLogsBasePackage path of the package
   */
  //Implementation note: This has been added, replacing constructor, to be able to inject this service as dependency
  void setChangeLogsBasePackage(String changeLogsBasePackage) {
    this.changeLogsBasePackage = changeLogsBasePackage;
  }

  /**
   * <p>
   * Indicates the changeLogs end systemVersion
   * </p>
   *
   * @param endVersion
   *          systemVersion to upgrading upgrading with (lower than this systemVersion)
   */
  // Implementation note: This has been added, replacing constructor, to be
  // able to inject this service as dependency
  void setEndVersion(String endVersion) {
    this.endVersion = new DefaultArtifactVersion(endVersion);
  }

  /**
   * <p>
   * Indicates the changeLogs start systemVersion
   * </p>
   *
   * @param startVersion
   *          systemVersion to start upgrading from (greater equals this systemVersion)
   */
  // Implementation note: This has been added, replacing constructor, to be
  // able to inject this service as dependency
  void setStartVersion(String startVersion) {
    this.startVersion = new DefaultArtifactVersion(startVersion);
  }

  @SuppressWarnings("unchecked")
  List<Class<?>> fetchChangeLogs() {
    Reflections reflections = new Reflections(changeLogsBasePackage);
    List<Class<?>> changeLogs = new ArrayList<>(reflections.getTypesAnnotatedWith(ChangeLog.class)); // TODO remove dependency, do own method

    Collections.sort(changeLogs, new ChangeLogComparator());

    return changeLogs;
  }

  @SuppressWarnings("unchecked")
  List<Method> fetchChangeSets(final Class<?> type) throws MongockException {
    final List<Method> changeSets = filterChangeSetAnnotation(asList(type.getDeclaredMethods()));

    Collections.sort(changeSets, new ChangeSetComparator());

    return changeSets;
  }

  boolean isRunAlwaysChangeSet(Method changesetMethod) {
    if (changesetMethod.isAnnotationPresent(ChangeSet.class)) {
      ChangeSet annotation = changesetMethod.getAnnotation(ChangeSet.class);
      return annotation.runAlways();
    } else {
      return false;
    }
  }

  /**
   * Quick implementation to generate a execution id. date plus uuid. The date is for easier human identification
   * @return unique execution id
   */
  String getNewExecutionId() {
    return String.format("%s.%s", LocalDateTime.now().toString(), UUID.randomUUID().toString());
  }

  ChangeEntry createChangeEntry(String executionId, Method changesetMethod) {
    if (changesetMethod.isAnnotationPresent(ChangeSet.class)) {
      ChangeSet annotation = changesetMethod.getAnnotation(ChangeSet.class);

      return new ChangeEntry(
          executionId,
          annotation.id(),
          annotation.author(),
          new Date(),
          changesetMethod.getDeclaringClass().getName(),
          changesetMethod.getName());
    } else {
      return null;
    }
  }

  /**
   * <p>It creates an instance from a given Class.</p>
   *
   * @param changelogClass class to create the instance from
   * @param <T>            Class parameter
   * @return an instance of the given class
   * @throws NoSuchMethodException     If reflection fails
   * @throws InvocationTargetException If reflection fails
   * @throws InstantiationException    If reflection fails
   */
  //Implementation note: It has been added as a more flexible way to get the changeLog objects and make easier testing.
  <T> T createInstance(Class<T> changelogClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    return changelogClass.getConstructor().newInstance();
  }


  private List<Method> filterChangeSetAnnotation(List<Method> allMethods) throws MongockException {
    final Set<String> changeSetIds = new HashSet<>();
    final List<Method> changesetMethods = new ArrayList<>();
    for (final Method method : allMethods) {
      if (method.isAnnotationPresent(ChangeSet.class)) {
        String id = method.getAnnotation(ChangeSet.class).id();
        if (changeSetIds.contains(id)) {
          throw new MongockException(String.format("Duplicated changeset id found: '%s'", id));
        }

        changeSetIds.add(id);
        String versionString = method.getAnnotation(ChangeSet.class).systemVersion();
        ArtifactVersion version = new DefaultArtifactVersion(versionString);
        if (version.compareTo(startVersion) >= 0 && version.compareTo(endVersion) < 0) {
          changesetMethods.add(method);
        }
      }
    }
    return changesetMethods;
  }

  private static class ChangeLogComparator implements Comparator<Class<?>>, Serializable {
    private static final long serialVersionUID = -358162121872177974L;

    @Override
    public int compare(Class<?> o1, Class<?> o2) {
      ChangeLog c1 = o1.getAnnotation(ChangeLog.class);
      ChangeLog c2 = o2.getAnnotation(ChangeLog.class);

      String val1 = !(hasText(c1.order())) ? o1.getCanonicalName() : c1.order();
      String val2 = !(hasText(c2.order())) ? o2.getCanonicalName() : c2.order();

      if (val1 == null && val2 == null) {
        return 0;
      } else if (val1 == null) {
        return -1;
      } else if (val2 == null) {
        return 1;
      }

      return val1.compareTo(val2);
    }
  }

  private static class ChangeSetComparator implements Comparator<Method>, Serializable {
    private static final long serialVersionUID = -854690868262484102L;

    @Override
    public int compare(Method o1, Method o2) {
      ChangeSet c1 = o1.getAnnotation(ChangeSet.class);
      ChangeSet c2 = o2.getAnnotation(ChangeSet.class);
      return c1.order().compareTo(c2.order());
    }
  }

}
