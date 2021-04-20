package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.withChangockAnnotations;


import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;

/**
 *
 * @since 27/07/2014
 */
@ChangeLog(order = "1")
public class ChangeLogwithChangockAnnotations {

  @ChangeSet(author = "testuser", id = "withChangockAnnotations", order = "01", systemVersion = "0.1")
  public void withChangockAnnotations() {
    System.out.println("invoked 1");
  }


}
