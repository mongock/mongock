package io.mongock.driver.mongodb.v3.changelogs.runalways;

import io.mongock.driver.api.common.SystemChange;
import org.junit.Assert;
import org.junit.Test;

public class MongockV3LegacyMigrationChangeRunAlwaysLogTest {

  @Test
  public void isAnnotated() {
    Assert.assertTrue(MongockV3LegacyMigrationChangeRunAlwaysLog.class.getAnnotation(SystemChange.class).updatesSystemTable());
  }

}
