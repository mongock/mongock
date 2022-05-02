package io.mongock.driver.core.driver;

public class ConnectionDriverTestingWrapper {

  private final ConnectionDriverBase connectionDriver;

  public ConnectionDriverTestingWrapper(ConnectionDriverBase connectionDriver) {
    this.connectionDriver = connectionDriver;
  }

  public void reset() {
    connectionDriver.resetInitialization();
  }

  public void cleanMigrationRepository() {
    connectionDriver.cleanMigrationRepository();
  }

  public void cleanLockRepository() {
    connectionDriver.cleanLockRepository();
  }

}
