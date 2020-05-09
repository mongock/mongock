package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.integration.test4;

import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeLog
public class ChangeLogWithMongoTemplate {

  @ChangeSet(author = "testuser", id = "shouldFailBecauseMongoTemplate", order = "00")
  public void shouldFailBecauseMongoTemplate(MongoTemplate mongoTemplate) {
  }

}
