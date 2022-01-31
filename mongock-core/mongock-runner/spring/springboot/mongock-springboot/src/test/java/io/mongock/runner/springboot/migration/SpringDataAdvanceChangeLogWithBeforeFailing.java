package io.mongock.runner.springboot.migration;

import com.mongodb.client.MongoCollection;
import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.concurrent.CountDownLatch;

@ChangeUnit(id="SpringDataAdvanceChangeLogWithBeforeFailing", order = "2", author = "mongock_test", systemVersion = "1")
public class SpringDataAdvanceChangeLogWithBeforeFailing {

  public static final String COLLECTION_NAME = SpringDataAdvanceChangeLogWithBeforeFailing.class.getSimpleName() + "Collection";

  public static boolean changeSetCalled = false;
  public static boolean rollbackCalled = false;
  public static boolean rollbackBeforeCalled = false;
  public final static CountDownLatch rollbackCalledLatch = new CountDownLatch(2);


  public static void clear() {
    changeSetCalled = false;
    rollbackCalled = false;
    rollbackBeforeCalled = false;
  }

  public SpringDataAdvanceChangeLogWithBeforeFailing(MongoTemplate template) {
  }

  @Execution
  public void changeSet() {
    changeSetCalled = true;
    rollbackCalled = false;
    rollbackBeforeCalled = false;
  }

  @RollbackExecution
  public void rollback() {
    rollbackCalled = true;
    rollbackCalledLatch.countDown();
  }


  @BeforeExecution
  public void before() {
    throw new RuntimeException("Expected exception in " + SpringDataAdvanceChangeLogWithBeforeFailing.class + " changeLog[Before]");
  }

  @RollbackBeforeExecution
  public void rollbackBefore() {
    rollbackBeforeCalled = true;
    rollbackCalledLatch.countDown();
  }

}
