package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.changelogs.runonce;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.changelogs.LegacyService;
import com.github.cloudyrock.mongock.migration.MongockLegacyMigration;
import com.mongodb.client.MongoDatabase;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.entry.ChangeEntryService;
import io.changock.migration.api.annotations.NonLockGuarded;
import io.changock.migration.api.annotations.NonLockGuardedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;

@ChangeLog(order = "00001")
public class MongockSync4LegacyMigrationChangeLog {

  private final static Logger logger = LoggerFactory.getLogger(MongockSync4LegacyMigrationChangeLog.class);

  @ChangeSet(id = "mongock-legacy-migration", author = "mongock", order = "00001")
  public void mongockSpringLegacyMigration(@NonLockGuarded(NonLockGuardedType.NONE)
                                           @Named("legacy-migration") MongockLegacyMigration legacyMigration,
                                           MongoDatabase mongoDatabase,
                                           ChangeEntryService<ChangeEntry> changeEntryService) {
    new LegacyService().executeMigration(legacyMigration, mongoDatabase, changeEntryService);
  }
}
