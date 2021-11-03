package io.mongock.driver.api.entry;

import com.google.common.collect.ImmutableSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum ChangeState {
  EXECUTED, FAILED, IGNORED, ROLLED_BACK, ROLLBACK_FAILED;


  public static final Set<ChangeState> RELEVANT_STATES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(EXECUTED, null, ROLLED_BACK)));
}
