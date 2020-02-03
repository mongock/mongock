package com.github.cloudyrock.mongock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

public class SpringChangeService extends ChangeService {

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
    return ProfileUtil.filterByActiveProfiles(activeProfiles, changeLogs);
  }

  @Override
  public List<Method> fetchChangeSets(Class<?> type) throws MongockException {
    final List<Method> changeSets = super.fetchChangeSets(type);
    return ProfileUtil.filterByActiveProfiles(activeProfiles, changeSets);
  }




}
