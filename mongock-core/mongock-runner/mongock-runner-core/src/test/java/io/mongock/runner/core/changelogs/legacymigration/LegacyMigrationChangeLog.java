package io.mongock.runner.core.changelogs.legacymigration;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import io.changock.migration.api.annotations.NonLockGuarded;
import io.mongock.api.config.LegacyMigration;

import javax.inject.Named;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@ChangeLog(order = "1")
public class LegacyMigrationChangeLog {

  public final static CountDownLatch latch = new CountDownLatch(1);

  @ChangeSet(author = "executor", id = "legacy_migration", order = "1", runAlways = true, systemVersion = "1")
  public void legacyMigration(@NonLockGuarded @Named("legacyMigration")List<LegacyMigration> legacyMigrations) {
    if(legacyMigrations == null ) {
      throw new RuntimeException("legacyMigration dependency null");
    }
    if(!"AUTHOR".equals(legacyMigrations.get(0).getMappingFields().getAuthor())) {
      throw new RuntimeException("wrong injected legacyMigration dependency ");
    }
    latch.countDown();
  }


}
