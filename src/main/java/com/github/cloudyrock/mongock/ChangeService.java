package com.github.cloudyrock.mongock;

import org.reflections.Reflections;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.springframework.util.StringUtils.hasText;

/**
 * Utilities to deal with reflections and annotations
 *
 * @author lstolowski
 * @since 27/07/2014
 */
class ChangeService {
  private static final String DEFAULT_PROFILE = "default";

  private String changeLogsBasePackage;
  private List<String> activeProfiles = Collections.singletonList(DEFAULT_PROFILE);

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
   * <p>Spring environment environment</p>
   *
   * @param environment environment
   */
  //Implementation note: This has been added, replacing constructor, to be able to inject this service as dependency
  void setEnvironment(Environment environment) {
    if (environment != null && environment.getActiveProfiles() != null && environment.getActiveProfiles().length > 0) {
      this.activeProfiles = asList(environment.getActiveProfiles());
    }
  }

  @SuppressWarnings("unchecked")
  List<Class<?>> fetchChangeLogs() {
    Reflections reflections = new Reflections(changeLogsBasePackage);
    Set<Class<?>> changeLogs =
        reflections.getTypesAnnotatedWith(ChangeLog.class); // TODO remove dependency, do own method
    List<Class<?>> filteredChangeLogs = (List<Class<?>>) filterByActiveProfiles(changeLogs);

    Collections.sort(filteredChangeLogs, new ChangeLogComparator());

    return filteredChangeLogs;
  }

  @SuppressWarnings("unchecked")
  List<Method> fetchChangeSets(final Class<?> type) throws MongockException {
    final List<Method> changeSets = filterChangeSetAnnotation(asList(type.getDeclaredMethods()));
    final List<Method> filteredChangeSets = (List<Method>) filterByActiveProfiles(changeSets);

    Collections.sort(filteredChangeSets, new ChangeSetComparator());

    return filteredChangeSets;
  }

  boolean isRunAlwaysChangeSet(Method changesetMethod) {
    if (changesetMethod.isAnnotationPresent(ChangeSet.class)) {
      ChangeSet annotation = changesetMethod.getAnnotation(ChangeSet.class);
      return annotation.runAlways();
    } else {
      return false;
    }
  }

  ChangeEntry createChangeEntry(Method changesetMethod) {
    if (changesetMethod.isAnnotationPresent(ChangeSet.class)) {
      ChangeSet annotation = changesetMethod.getAnnotation(ChangeSet.class);

      return new ChangeEntry(
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

  private boolean matchesActiveSpringProfile(AnnotatedElement element) {
    if (!isProfileAnnotationPresent()) {
      return true;
    }
    if (!element.isAnnotationPresent(Profile.class)) {
      return true; // no-profiled changeset always matches
    }
    List<String> profiles = asList(element.getAnnotation(Profile.class).value());
    for (String profile : profiles) {
      if (profile != null && profile.length() > 0 && profile.charAt(0) == '!') {
        if (!activeProfiles.contains(profile.substring(1))) {
          return true;
        }
      } else if (activeProfiles.contains(profile)) {
        return true;
      }
    }
    return false;
  }

  private List<?> filterByActiveProfiles(Collection<? extends AnnotatedElement> annotated) {
    List<AnnotatedElement> filtered = new ArrayList<>();
    for (AnnotatedElement element : annotated) {
      if (matchesActiveSpringProfile(element)) {
        filtered.add(element);
      }
    }
    return filtered;
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
        changesetMethods.add(method);
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
