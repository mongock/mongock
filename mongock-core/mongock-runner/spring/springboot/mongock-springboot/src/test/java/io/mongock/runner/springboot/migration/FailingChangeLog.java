package io.mongock.runner.springboot.migration;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import io.mongock.runner.springboot.domain.Client;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.stream.IntStream;

@ChangeLog(order = "2")
public class FailingChangeLog {

  @ChangeSet(id = "method-successful", order = "001", author = "mongock")
  public void methodSuccessful(MongoTemplate mongoTemplate) {
    IntStream.range(0, 10)
        .mapToObj(i -> new Client("name-" + i, "email-" + i, "phone" + i, "country" + i))
        .forEach(client -> mongoTemplate.save(client, Client.COLLECTION_NAME));
  }

  @ChangeSet(id = "method-failing", order = "002", author = "mongock")
  public void methodFailing() {
    if (true) {
      throw new RuntimeException("Transaction error");
    }
  }


}
