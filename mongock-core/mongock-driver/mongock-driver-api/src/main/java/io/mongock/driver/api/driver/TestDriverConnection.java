package io.mongock.driver.api.driver;


import io.mongock.driver.api.entry.ChangeEntry;

/**
 * Wrapper util class for testing
 */
public class TestDriverConnection {

  private final ConnectionDriver driver;

  public TestDriverConnection(ConnectionDriver driver) {
    this.driver = driver;
  }

  public void reset() {
    driver.getLockManager().clean();
    driver.getLockManager().acquireLockDefault();
  }

  public ConnectionDriver getDriver() {
    return driver;
  }
}
