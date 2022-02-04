package io.mongock.driver.mongodb.async.changelogs.runnonce;

import io.mongock.driver.api.common.SystemChange;
import io.mongock.driver.mongodb.async.changelogs.runonce.MongockReactiveLegacyMigrationChange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MongockReactiveLegacyMigrationChangeLogTest {

  @Test
  public void isAnnotated() {
    Assertions.assertTrue(MongockReactiveLegacyMigrationChange.class.getAnnotation(SystemChange.class).updatesSystemTable());
  }

}
