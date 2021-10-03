package io.mongock.runner.spring.base.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.function.Function;

public final class ProfileUtil {

  private ProfileUtil() {
  }

  private static boolean isNegativeProfile(String profile) {
    return profile.charAt(0) == '!';
  }

  private static boolean containsProfile(List<String> activeProfiles, String profile) {
    return activeProfiles.contains(profile);
  }

  private static boolean containsNegativeProfile(List<String> activeProfiles, String profile) {
    return ProfileUtil.containsProfile(activeProfiles, profile.substring(1));
  }

  public static boolean matchesActiveSpringProfile(List<String> activeProfiles,
                                                   Class<? extends Annotation> annotation,
                                                   AnnotatedElement element,
                                                   Function<AnnotatedElement, String[]> profilExtractor) {
    if (!element.isAnnotationPresent(annotation)) {
      return true; // no-profiled changeset always matches
    }
    boolean containsActiveProfile = false;
    for (String profile : profilExtractor.apply(element)) {
      if ((profile == null || "".equals(profile))) {
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
}
