package com.github.cloudyrock.mongock.test.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.decorator.impl.MongockTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 *
 */
@ChangeLog
public class SpringDataChangelog {

  @ChangeSet(author = "abelski", id = "withMongoTemplate", order = "04")
  public void testChangeSet(MongoTemplate mongoTemplate) {
    System.out.println("invoked  with mongoTemplate=" + mongoTemplate.toString());
    System.out.println("invoked  with mongoTemplate=" + mongoTemplate.getCollectionNames());
  }

  @ChangeSet(author = "abelski", id = "withMongockTemplate", order = "04")
  public void withMongockTemplate(MongockTemplate mongoTemplate) {
    System.out.println("invoked  with mongockTemplate=" + mongoTemplate.toString());
    System.out.println("invoked  with mongockTemplate=" + mongoTemplate.getCollectionNames());
  }
}
