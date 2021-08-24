package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.withRollback;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.interfaces.BasicChangeLog;

import java.util.concurrent.CountDownLatch;

@ChangeLog(order = "1")
public class BasicChangeLogWithRollback implements BasicChangeLog {

  public final static CountDownLatch rollbackCalledLatch = new CountDownLatch(2);

  @Override
  public String geId() {
    return "changeset_with_rollback_1";
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

    //TODO NOTHING
  }

  @Override
  public void rollback() {
    rollbackCalledLatch.countDown();
  }


}
