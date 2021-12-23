package io.mongock.runner.core.builder;

import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.api.driver.DriverLegaciable;

public interface LegaciableConnectionDriver extends ConnectionDriver, DriverLegaciable {
}
