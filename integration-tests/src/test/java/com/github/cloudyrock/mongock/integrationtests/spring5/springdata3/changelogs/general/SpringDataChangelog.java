package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.general;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;


@ChangeLog
public class SpringDataChangelog {
  @ChangeSet(author = "mongock", id = "spring_test4", order = "04")
  public void testChangeSet(MongockTemplate mongoTemplate) {
    System.out.println("invoked  with mongoTemplate=" + mongoTemplate.toString());
    System.out.println("invoked  with mongoTemplate=" + mongoTemplate.getCollectionNames());
  }
}
