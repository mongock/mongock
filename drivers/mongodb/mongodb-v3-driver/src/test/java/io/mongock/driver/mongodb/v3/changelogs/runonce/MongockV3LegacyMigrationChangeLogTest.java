package io.mongock.driver.mongodb.v3.changelogs.runonce;

import io.mongock.driver.api.common.SystemChange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MongockV3LegacyMigrationChangeLogTest {

  @Test
  public void isAnnotated() {
    Assertions.assertTrue(MongockV3LegacyMigrationChangeLog.class.getAnnotation(SystemChange.class).updatesSystemTable());
  }
}
