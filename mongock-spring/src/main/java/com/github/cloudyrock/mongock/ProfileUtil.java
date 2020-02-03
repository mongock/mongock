package com.github.cloudyrock.mongock;

import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class ProfileUtil {

  private ProfileUtil(){
  }

  static boolean isNegativeProfile(String profile) {
    return profile.charAt(0) == '!';
  }

  static boolean containsProfile(List<String> activeProfiles, String profile) {
    return activeProfiles.contains(profile);
  }

  static boolean containsNegativeProfile(List<String> activeProfiles, String profile) {
    return ProfileUtil.containsProfile(activeProfiles, profile.substring(1));
  }

  static boolean matchesActiveSpringProfile(List<String> activeProfiles, AnnotatedElement element) {
    if (!element.isAnnotationPresent(Profile.class)) {
      return true; // no-profiled changeset always matches
    }
    boolean containsActiveProfile = false;
    for (String profile : element.getAnnotation(Profile.class).value()) {
      if (StringUtils.isEmpty(profile)) {
        continue;
      }
      if (ProfileUtil.isNegativeProfile(profile)) {
        if (ProfileUtil.containsNegativeProfile(activeProfiles, profile)) {
          return false;
        }
      } else {
        containsActiveProfile = true;
        if (ProfileUtil.containsProfile(activeProfiles, profile)) {
          return true;
        }
      }
    }
    return !containsActiveProfile;
  }


  static <T extends AnnotatedElement> List<T> filterByActiveProfiles(List<String> activeProfiles, Collection<T> annotated) {
    List<T> filtered = new ArrayList<>();
    for (T element : annotated) {
      if (ProfileUtil.matchesActiveSpringProfile(activeProfiles, element)) {
        filtered.add(element);
      }
    }
    return filtered;
  }

}
