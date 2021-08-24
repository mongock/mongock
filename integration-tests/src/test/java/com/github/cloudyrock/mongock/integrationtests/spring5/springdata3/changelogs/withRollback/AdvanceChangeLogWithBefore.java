package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.withRollback;

import com.github.cloudyrock.mongock.interfaces.ChangeLog;

import java.util.concurrent.CountDownLatch;


public class AdvanceChangeLogWithBefore implements ChangeLog {

  public static boolean rollbackCalled = false;
  public static boolean rollbackBeforeCalled = false;
  public final static CountDownLatch rollbackCalledLatch = new CountDownLatch(2);

  @Override
  public String geId() {
    return "AdvanceChangeLogWithBefore";
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
    rollbackCalled = false;
    rollbackBeforeCalled = false;
  }

  @Override
  public void rollback() {
    rollbackCalled = true;
    rollbackCalledLatch.countDown();
  }


  @Override
  public void before() {
    System.nanoTime();
  }

  @Override
  public void rollbackBefore() {
    rollbackBeforeCalled = true;
    rollbackCalledLatch.countDown();

  }

}
