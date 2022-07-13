package io.mongock.driver.mongodb.v3.changelogs.runalways;

import io.mongock.driver.api.common.SystemChange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MongockV3LegacyMigrationChangeRunAlwaysLogTest {

  @Test
  public void isAnnotated() {
    Assertions.assertTrue(MongockV3LegacyMigrationChangeRunAlwaysLog.class.getAnnotation(SystemChange.class).updatesSystemTable());
  }

}
