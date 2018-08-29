package com.github.cloudyrock.mongock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

public class SpringChangeService extends ChangeService {
  private static final Logger log = LoggerFactory.getLogger(SpringChangeService.class);

  private static final String DEFAULT_PROFILE = "default";

  private List<String> activeProfiles = Collections.singletonList(DEFAULT_PROFILE);

  /**
   * <p>Spring environment environment</p>
   *
   * @param environment environment
   */
  void setEnvironment(Environment environment) {
    if (environment != null && environment.getActiveProfiles() != null && environment.getActiveProfiles().length > 0) {
      this.activeProfiles = asList(environment.getActiveProfiles());
    }
  }

  @Override
  public List<Class<?>> fetchChangeLogs() {
    List<Class<?>> changeLogs = super.fetchChangeLogs();
    return filterByActiveProfiles(changeLogs);
  }

  @Override
  public List<Method> fetchChangeSets(Class<?> type) throws MongockException {
    final List<Method> changeSets = super.fetchChangeSets(type);
    return filterByActiveProfiles(changeSets);
  }


  private <T extends AnnotatedElement> List<T> filterByActiveProfiles(Collection<T> annotated) {
    List<T> filtered = new ArrayList<>();
    for (T element : annotated) {
      if (matchesActiveSpringProfile(element)){
        filtered.add(element);
      }
    }
    return filtered;
  }
  private boolean matchesActiveSpringProfile(AnnotatedElement element) {
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
}
