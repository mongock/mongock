package io.mongock.runner.springboot.migration;

import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.runner.springboot.domain.Client;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.stream.IntStream;

@ChangeUnit(id = "TransactionSuccessfulChangeLog", order = "2", author = "mongock")
public class TransactionSuccessfulChangeLog {

  @BeforeExecution
  public void createCollection(MongoTemplate template) {
    if(!template.collectionExists(Client.COLLECTION_NAME)) {
      template.createCollection(Client.COLLECTION_NAME);
    }
  }

  @RollbackBeforeExecution
  public void dropCollection(MongoTemplate template) {
    if(template.collectionExists(Client.COLLECTION_NAME)) {
      template.dropCollection(Client.COLLECTION_NAME);
    }
  }

  @Execution
  public void execution(MongoTemplate template) {
    IntStream.range(0, 10)
        .mapToObj(i -> new Client("name-" + i, "email-" + i, "phone" + i, "country" + i))
        .forEach(client -> template.save(client, Client.COLLECTION_NAME));
  }

  @RollbackExecution
  public void rollback() {

  }

}
