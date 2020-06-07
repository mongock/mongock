package com.github.cloudyrock.spring.v5.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.spring.v5.MongockConfiguration;
import io.changock.migration.api.annotations.NonLockGuarded;
import io.changock.migration.api.annotations.NonLockGuardedType;

@ChangeLog(order = "00001")
public class LegacyMigrationChangeLog {


  @ChangeSet(id = "mongock-legacy-migration", author = "mongock", order = "00001", runAlways = true)
  public void mongockSpringLegacyMigration(
      @NonLockGuarded(NonLockGuardedType.NONE)MongockConfiguration.MongockLegacyMigration legacyMigration) {
    System.out.println("\n\n\nLEGACY MIGRATION\n\n\n");

  }
}
