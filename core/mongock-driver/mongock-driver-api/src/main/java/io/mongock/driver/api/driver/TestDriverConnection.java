package io.mongock.driver.api.driver;


import io.mongock.driver.api.entry.ChangeEntry;

/**
 * Wrapper util class for testing
 */
public class TestDriverConnection<CHENGE_ENTRY extends ChangeEntry> {

  private final ConnectionDriver<CHENGE_ENTRY> driver;

  public TestDriverConnection(ConnectionDriver driver) {
    this.driver = driver;
  }

  public void reset() {
    driver.getLockManager().clean();
    driver.getLockManager().acquireLockDefault();
  }

  public ConnectionDriver<CHENGE_ENTRY> getDriver() {
    return driver;
  }
}
