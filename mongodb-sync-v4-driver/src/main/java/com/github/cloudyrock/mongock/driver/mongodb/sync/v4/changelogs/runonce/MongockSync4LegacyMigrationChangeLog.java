package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.changelogs.runonce;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import io.changock.migration.api.annotations.NonLockGuarded;
import io.changock.migration.api.annotations.NonLockGuardedType;
import com.github.cloudyrock.mongock.config.LegacyMigration;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntryService;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.changelogs.LegacyService;
import com.mongodb.client.MongoDatabase;

import javax.inject.Named;

@ChangeLog(order = "00001")
public class MongockSync4LegacyMigrationChangeLog {


  @ChangeSet(id = "mongock-legacy-migration", author = "mongock", order = "00001")
  public void mongockSpringLegacyMigration(@NonLockGuarded(NonLockGuardedType.NONE)
                                           @Named("legacy-migration") LegacyMigration legacyMigration,
                                           MongoDatabase mongoDatabase,
                                           ChangeEntryService<ChangeEntry> changeEntryService) {
    new LegacyService().executeMigration(legacyMigration, mongoDatabase, changeEntryService);
  }
}
