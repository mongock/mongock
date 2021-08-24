package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.withRollback;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.interfaces.BasicChangeLog;

@ChangeLog(order = "1")
public class BasicChangeLogWithExceptionInRollback implements BasicChangeLog {


  @Override
  public String geId() {
    return "changeset_with_exception_in_rollback_1";
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
    if(true) throw new MongockException();
  }

  @Override
  public void rollback() {
    if(true) throw new MongockException();
  }


}
