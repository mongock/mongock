package io.mongock.driver.mongodb.v3.changelogs.runonce;

import io.mongock.driver.api.common.SystemChange;
import org.junit.Assert;
import org.junit.Test;

public class MongockV3LegacyMigrationChangeLogTest {

  @Test
  public void isAnnotated() {
    Assert.assertTrue(MongockV3LegacyMigrationChangeLog.class.getAnnotation(SystemChange.class).updatesSystemTable());
  }
}
