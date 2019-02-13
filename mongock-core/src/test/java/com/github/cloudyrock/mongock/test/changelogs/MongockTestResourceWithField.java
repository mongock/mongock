package com.github.cloudyrock.mongock.test.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.MongockTest;

/**
 *
 * @since 27/07/2014
 */
@ChangeLog(order = "1")
public class MongockTestResourceWithField {

  private MongockTest.ObjectToVerify object;

  public MongockTestResourceWithField() {
    this.object = new MongockTest.ObjectToVerify();
  }

  public MongockTestResourceWithField(MongockTest.ObjectToVerify object) {
    this.object = object;
  }

  @ChangeSet(author = "testuser", id = "Ctest1", order = "01", runAlways = true)
  public void testChangeSet() {

    object.methodToVerify();

  }


}
