package io.mongock.integrationtests.spring5.springdata3.changelogs.interfaces.springdata.rollback;

import com.mongodb.client.MongoCollection;

import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ChangeUnit(id="SpringDataAdvanceChangeLogWithBeforeFailing", order = "2", author = "mongock_test", systemVersion = "1")
public class SpringDataAdvanceChangeLogWithBeforeFailing {

  public static final String COLLECTION_NAME = SpringDataAdvanceChangeLogWithBeforeFailing.class.getSimpleName() + "Collection";

  public static boolean changeSetCalled = false;
  public static boolean rollbackCalled = false;
  public static boolean rollbackBeforeCalled = false;
  public final static CountDownLatch rollbackCalledLatch = new CountDownLatch(2);


  private final MongoTemplate template;
  private MongoCollection<Document> clientCollection;

  public static void clear() {
    changeSetCalled = false;
    rollbackCalled = false;
    rollbackBeforeCalled = false;
  }

  public SpringDataAdvanceChangeLogWithBeforeFailing(MongoTemplate template) {
    this.template = template;
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
