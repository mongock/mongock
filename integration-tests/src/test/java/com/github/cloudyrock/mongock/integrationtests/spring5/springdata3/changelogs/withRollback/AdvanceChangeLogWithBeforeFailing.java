package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.withRollback;

import com.github.cloudyrock.mongock.interfaces.ChangeLog;

import java.util.concurrent.CountDownLatch;


public class AdvanceChangeLogWithBeforeFailing implements ChangeLog {

  public final static CountDownLatch rollbackCalledLatch = new CountDownLatch(2);
  @Override
  public String geId() {
    return "AdvanceChangeLogWithBeforeFailing";
  }

  @Override
  public String getAuthor() {
    return "mongock_test";
  }

  @Override
  public String getOrder() {
    return "1";
  }

  @Override
  public boolean isFailFast() {
    return true;
  }

  @Override
  public String getSystemVersion() {
    return "1";
  }

  @Override
  public void changeSet() {
  }

  @Override
  public void rollback() {
    rollbackCalledLatch.countDown();
  }


  @Override
  public void before() {

    if(true) throw new RuntimeException("Expected exception in " + AdvanceChangeLogWithBeforeFailing.class + " changeLog[ChangeSet]");
  }

  @Override
  public void rollbackBefore() {
    rollbackCalledLatch.countDown();
  }

}
