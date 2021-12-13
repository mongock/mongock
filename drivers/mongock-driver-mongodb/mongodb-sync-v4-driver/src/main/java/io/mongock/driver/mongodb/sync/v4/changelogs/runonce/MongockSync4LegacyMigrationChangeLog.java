package io.mongock.driver.mongodb.sync.v4.changelogs.runonce;

import com.mongodb.client.MongoDatabase;
import io.changock.migration.api.annotations.NonLockGuarded;
import io.changock.migration.api.annotations.NonLockGuardedType;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.api.config.LegacyMigration;
import io.mongock.driver.api.common.SystemChange;
import io.mongock.driver.api.entry.ChangeEntryService;
import io.mongock.driver.mongodb.sync.v4.changelogs.LegacyService;

import javax.inject.Named;

@SystemChange(updatesSystemTable = true)
@ChangeUnit(id = "mongock-legacy-migration", author = "mongock", order = "00001")
public class MongockSync4LegacyMigrationChangeLog {

  @Execution
  public void mongockSpringLegacyMigration(@NonLockGuarded(NonLockGuardedType.NONE)
                                           @Named("legacy-migration") LegacyMigration legacyMigration,
                                           MongoDatabase mongoDatabase,
                                           ChangeEntryService changeEntryService) {
    new LegacyService().executeMigration(legacyMigration, mongoDatabase, changeEntryService);
  }

  @RollbackExecution
  public void rollbackIgnored() {
  }
}
