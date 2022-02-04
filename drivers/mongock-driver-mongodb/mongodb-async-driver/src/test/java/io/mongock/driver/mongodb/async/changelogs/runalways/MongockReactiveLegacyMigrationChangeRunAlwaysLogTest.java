package io.mongock.driver.mongodb.async.changelogs.runalways;

import io.mongock.driver.api.common.SystemChange;
import io.mongock.driver.mongodb.async.changelogs.runalways.MongockReactiveLegacyMigrationRunAlwaysChange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MongockReactiveLegacyMigrationChangeRunAlwaysLogTest {

  @Test
  public void isAnnotated() {
    Assertions.assertTrue(MongockReactiveLegacyMigrationRunAlwaysChange.class.getAnnotation(SystemChange.class).updatesSystemTable());

  }

}
