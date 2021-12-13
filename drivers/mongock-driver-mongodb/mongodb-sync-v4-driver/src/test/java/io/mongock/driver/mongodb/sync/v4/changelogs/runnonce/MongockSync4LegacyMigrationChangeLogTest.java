package io.mongock.driver.mongodb.sync.v4.changelogs.runnonce;

import io.mongock.driver.api.common.SystemChange;
import io.mongock.driver.mongodb.sync.v4.changelogs.runalways.MongockSync4LegacyMigrationChangeRunAlwaysLog;
import io.mongock.driver.mongodb.sync.v4.changelogs.runonce.MongockSync4LegacyMigrationChangeLog;
import org.junit.Assert;
import org.junit.Test;

public class MongockSync4LegacyMigrationChangeLogTest {

  @Test
  public void isAnnotated() {
    Assert.assertTrue(MongockSync4LegacyMigrationChangeLog.class.getAnnotation(SystemChange.class).updatesSystemTable());
  }

}
