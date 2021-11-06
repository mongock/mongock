package io.mongock.driver.mongodb.v3.changelogs.runonce;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import io.changock.migration.api.annotations.NonLockGuarded;
import io.changock.migration.api.annotations.NonLockGuardedType;
import io.mongock.api.config.LegacyMigration;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.driver.api.entry.ChangeEntryService;
import io.mongock.driver.mongodb.v3.changelogs.LegacyService;
import com.mongodb.client.MongoDatabase;

import javax.inject.Named;

@ChangeLog(order = "00001")
public class MongockV3LegacyMigrationChangeLog extends LegacyService {

  @ChangeSet(id = "mongock-legacy-migration", author = "mongock", order = "00001")
  public void mongockSpringLegacyMigration(@NonLockGuarded(NonLockGuardedType.NONE)
                                           @Named("legacy-migration") LegacyMigration legacyMigration,
                                           MongoDatabase mongoDatabase,
                                           ChangeEntryService changeEntryService) {
    new LegacyService().executeMigration(legacyMigration, mongoDatabase, changeEntryService);
  }

}
