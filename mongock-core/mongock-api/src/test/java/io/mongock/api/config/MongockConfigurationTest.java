package io.mongock.api.config;

import io.mongock.utils.Constants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MongockConfigurationTest {

  @Test
  public void shouldKeepBuggyCopyDefaultMigrationAuthorFromExisitingConfigurationDefault() {
    MongockConfiguration conf = new MongockConfiguration();

    MongockConfiguration newConf = new MongockConfiguration();
    conf.setDefaultMigrationAuthor("another author");
    newConf.updateFrom(conf);
    assertEquals(MongockConfiguration.DEFAULT_MIGRATION_AUTHOR, newConf.getDefaultMigrationAuthor());
  }

  @Test
  public void shouldCopyDefaultAuthorFromExisitingConfigurationIfSet() {
    MongockConfiguration conf = new MongockConfiguration();
    conf.setDefaultAuthor("joey ramone");

    MongockConfiguration newConf = new MongockConfiguration();
    newConf.updateFrom(conf);
    assertEquals("joey ramone", newConf.getDefaultAuthor());
  }

  @Test
  public void shouldCopyDefaultAuthorFromExisitingConfigurationIfNotSet() {
    MongockConfiguration conf = new MongockConfiguration();

    MongockConfiguration newConf = new MongockConfiguration();
    newConf.updateFrom(conf);
    assertEquals(MongockConfiguration.DEFAULT_AUTHOR, newConf.getDefaultAuthor());
  }

  @Test
  public void shouldReturnDefaultMigrationAuthor_IfNotSet() {
    MongockConfiguration conf = new MongockConfiguration();
    assertEquals(MongockConfiguration.DEFAULT_AUTHOR, conf.getDefaultAuthor());
  }

  @Test
  public void shouldReturnNewDefaultMigrationAuthor_IfNewProperty() {
    MongockConfiguration conf = new MongockConfiguration();
    conf.setDefaultAuthor("joey ramone");
    assertEquals("joey ramone", conf.getDefaultAuthor());
  }

  @Test
  public void shouldReturnNewQuitTryingAfterMillis_IfNewProperty() {
    MongockConfiguration conf = new MongockConfiguration();
    conf.setLockQuitTryingAfterMillis(1000L);
    conf.setMaxWaitingForLockMinutes(1);
    conf.setMaxTries(4);
    assertEquals(1000L, conf.getLockQuitTryingAfterMillis());
  }

  @Test
  public void shouldReturnLegacyQuitTryingAfterMillis_IfNewPropertyNotSet() {
    MongockConfiguration conf = new MongockConfiguration();
    int maxWaitingForLockMinutes = 1;
    int maxTries = 4;
    conf.setMaxWaitingForLockMinutes(maxWaitingForLockMinutes);
    conf.setMaxTries(maxTries);
    assertEquals(maxTries * maxWaitingForLockMinutes * 60 * 1000L, conf.getLockQuitTryingAfterMillis());
  }

  @Test
  public void shouldReturnLegacyQuitTryingAfterMillisWithDefaultMaxTries_IfNewPropertyAndMaxTriesNotSet() {
    MongockConfiguration conf = new MongockConfiguration();
    int maxWaitingForLockMinutes = 1;
    int defaultMaxTries = 3;
    conf.setMaxWaitingForLockMinutes(maxWaitingForLockMinutes);
    assertEquals(defaultMaxTries * maxWaitingForLockMinutes * 60 * 1000L, conf.getLockQuitTryingAfterMillis());
  }

  @Test
  public void shouldReturnDefaultQuitTryingAfterMillis_WhenNothingIsSet() {
    assertEquals(Constants.DEFAULT_QUIT_TRYING_AFTER_MILLIS, new MongockConfiguration().getLockQuitTryingAfterMillis());
  }

  @Test
  public void shouldReturnNewLockAcquired() {
    MongockConfiguration mongockConfiguration = new MongockConfiguration();
    mongockConfiguration.setLockAcquiredForMinutes(1);
    mongockConfiguration.setLockAcquiredForMillis(3000L);
    assertEquals(3000L, mongockConfiguration.getLockAcquiredForMillis());
  }

  @Test
  public void shouldReturnLegacyLockAcquired_WhenIsSetLast() {
    MongockConfiguration mongockConfiguration = new MongockConfiguration();
    int minutes = 2;
    mongockConfiguration.setLockAcquiredForMillis(3000L);
    mongockConfiguration.setLockAcquiredForMinutes(minutes);
    assertEquals(minutes * 60 * 1000L, mongockConfiguration.getLockAcquiredForMillis());
  }

  @Test
  public void shouldReturnDefaultLockAcquired_WhenNothingIsSet() {
    assertEquals(60 * 1000L, new MongockConfiguration().getLockAcquiredForMillis());
  }

  @Test
  public void shouldReturnDefaultLockTryFrequency_WhenNothingIsSet() {
    assertEquals(1000L, new MongockConfiguration().getLockTryFrequencyMillis());
  }
}
