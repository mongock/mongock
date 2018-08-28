package com.github.cloudyrock.mongock.test.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 *
 */
@ChangeLog
public class SpringDataChangelog {
  @ChangeSet(author = "abelski", id = "spring_test4", order = "04")
  public void testChangeSet(MongoTemplate mongoTemplate) {
    System.out.println("invoked  with mongoTemplate=" + mongoTemplate.toString());
    System.out.println("invoked  with mongoTemplate=" + mongoTemplate.getCollectionNames());
  }
}
