package com.github.cloudyrock.mongock;

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

}
