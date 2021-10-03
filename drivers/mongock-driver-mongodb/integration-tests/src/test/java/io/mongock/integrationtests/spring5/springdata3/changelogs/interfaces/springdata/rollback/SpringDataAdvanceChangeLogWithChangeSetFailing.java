package io.mongock.integrationtests.spring5.springdata3.changelogs.interfaces.springdata.rollback;

import com.mongodb.client.MongoCollection;

import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.integrationtests.spring5.springdata3.client.ClientRepository;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ChangeUnit(id="SpringDataAdvanceChangeLogWithChangeSetFailing", order = "2", author = "mongock_test", systemVersion = "1")
public class SpringDataAdvanceChangeLogWithChangeSetFailing {

  public static final String COLLECTION_NAME = SpringDataAdvanceChangeLogWithChangeSetFailing.class.getSimpleName() + "Collection";

  public static boolean rollbackCalled = false;
  public static boolean rollbackBeforeCalled = false;
  public final static CountDownLatch rollbackCalledLatch = new CountDownLatch(2);


  private final MongoTemplate template;
  private final ClientRepository clientRepository;
  private MongoCollection<Document> clientCollection;

  public static void clear() {
    rollbackCalled = false;
    rollbackBeforeCalled = false;
  }

  public SpringDataAdvanceChangeLogWithChangeSetFailing(MongoTemplate template, ClientRepository clientRepository) {
    this.template = template;
    this.clientRepository = clientRepository;
  }

  @Execution
  public void changeSet() {
    rollbackCalled = false;
    rollbackBeforeCalled = false;
    List<Document> clients = IntStream.range(0, 10)
        .mapToObj(i -> new Document().append("name","name-" + i).append("email", "email-" + i).append("phone", "phone" + i).append("country", "country" + i))
        .collect(Collectors.toList());
    template.getCollection(SpringDataAdvanceChangeLogWithChangeSetFailing.COLLECTION_NAME).insertMany(clients);
    if(true) throw new RuntimeException("Expected exception in " + SpringDataAdvanceChangeLogWithChangeSetFailing.class + " changeLog[ChangeSet]");
  }

  @RollbackExecution
  public void rollback() {
    rollbackCalled = true;
    rollbackCalledLatch.countDown();
  }

  @BeforeExecution
  public void before() {
    //creates the collection
    clientCollection = template.createCollection(SpringDataAdvanceChangeLogWithChangeSetFailing.COLLECTION_NAME);
  }

  @RollbackBeforeExecution
  public void rollbackBefore() {
    rollbackBeforeCalled = true;
    rollbackCalledLatch.countDown();
  }

}
