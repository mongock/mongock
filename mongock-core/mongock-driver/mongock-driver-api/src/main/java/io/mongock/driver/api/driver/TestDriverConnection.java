package io.mongock.driver.api.driver;

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
