package io.mongock.driver.mongodb.reactive.changelogs.runalways;

import io.mongock.driver.api.common.SystemChange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MongockReactiveLegacyMigrationChangeRunAlwaysLogTest {

  @Test
  public void isAnnotated() {
    Assertions.assertTrue(MongockReactiveLegacyMigrationRunAlwaysChange.class.getAnnotation(SystemChange.class).updatesSystemTable());

  }

}
