package io.mongock.runner.core.event;

import io.mongock.runner.core.event.result.MigrationResult;

public interface MongockResultEvent {

  MigrationResult getMigrationResult();
}
