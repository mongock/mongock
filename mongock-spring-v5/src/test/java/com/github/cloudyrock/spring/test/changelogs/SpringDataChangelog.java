package com.github.cloudyrock.spring.test.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;

/**
 *
 */
@ChangeLog
public class SpringDataChangelog {
  @ChangeSet(author = "abelski", id = "spring_test4", order = "04")
  public void testChangeSet(MongockTemplate mongoTemplate) {
    System.out.println("invoked  with mongoTemplate=" + mongoTemplate.toString());
    System.out.println("invoked  with mongoTemplate=" + mongoTemplate.getCollectionNames());
  }
}
