package io.mongock.driver.mongodb.sync.v4.changelogs.runnonce;

import io.mongock.driver.api.common.SystemChange;
import io.mongock.driver.mongodb.sync.v4.changelogs.runonce.MongockSync4LegacyMigrationChangeLog;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MongockSync4LegacyMigrationChangeLogTest {

  @Test
  public void isAnnotated() {
    Assertions.assertTrue(MongockSync4LegacyMigrationChangeLog.class.getAnnotation(SystemChange.class).updatesSystemTable());
  }

}
