package io.mongock.runner.springboot.migration;

import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.runner.springboot.domain.Client;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.stream.IntStream;

@ChangeUnit(id = "RunAlwaysSuccessfulChangeUnit", order = "2", author = "mongock", runAlways = true)
public class RunAlwaysSuccessfulChangeUnit {

  @Execution
  public void execution(MongoTemplate template) {
  }

  @RollbackExecution
  public void rollback() {

  }

}
