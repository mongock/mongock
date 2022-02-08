package io.mongock.driver.mongodb.sync.v4.changelogs.runalways;

import io.mongock.driver.api.common.SystemChange;
import org.junit.Assert;
import org.junit.Test;

public class MongockSync4LegacyMigrationChangeRunAlwaysLogTest {


  @Test
  public void isAnnotated() {
    Assert.assertTrue(MongockSync4LegacyMigrationChangeRunAlwaysLog.class.getAnnotation(SystemChange.class).updatesSystemTable());
  }

}
