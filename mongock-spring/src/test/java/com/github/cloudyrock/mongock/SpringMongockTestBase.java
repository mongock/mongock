package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.test.changelogs.MongockTestResource;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.GenericContainer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * Class to provide common configuration for Mongock**Test
 *
 *
 * @since 04/04/2018
 */
public class SpringMongockTestBase {

  static final String CHANGELOG_COLLECTION_NAME = "dbchangelog";

  protected SpringMongock runner;

  protected MongoDatabase mongoDatabase;

  @Mock
  protected ChangeEntryMongoRepository changeEntryRepository;

  @Mock
  protected LockChecker lockChecker;

  @Mock
  protected LockRepository lockRepository;

  @Spy
  protected SpringChangeService changeService;

  protected MongoClient mongoClient;

  @Mock
  private MongoRepositoryBase indexDao;


  protected static final String MONGO_CONTAINER = "mongo:3.1.5";
  protected static final Integer MONGO_PORT = 27017;
  protected static final String DEFAULT_DATABASE_NAME = "mongocktest";

  @Rule
  public GenericContainer mongo = new GenericContainer(MONGO_CONTAINER).withExposedPorts(MONGO_PORT);


  @Before
  public final void setUpParent() {
    mongoClient = new MongoClient(mongo.getContainerIpAddress(), mongo.getFirstMappedPort());
    mongoDatabase = mongoClient.getDatabase(DEFAULT_DATABASE_NAME);
    com.github.cloudyrock.mongock.TestUtils.setField(changeEntryRepository, "mongoDatabase", mongoDatabase);

    doCallRealMethod().when(changeEntryRepository).save(any(ChangeEntry.class));
    TestUtils.setField(changeEntryRepository, "indexDao", indexDao);
    TestUtils.setField(changeEntryRepository, "changelogCollectionName", CHANGELOG_COLLECTION_NAME);
    TestUtils.setField(changeEntryRepository, "collection", mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME));

    changeService.setChangeLogsBasePackage(MongockTestResource.class.getPackage().getName());

    SpringMongock temp = new SpringMongock(
        changeEntryRepository,
        mongoClient,
        changeService,
        lockChecker);

    temp.addChangeSetDependency(MongoDatabase.class, mongoDatabase);
    temp.addChangeSetDependency(MongoTemplate.class, new MongoTemplate(mongoClient, "mongocktest"));
    temp.setEnabled(true);
    temp.setThrowExceptionIfCannotObtainLock(true);
    temp.addChangeSetDependency(Environment.class, Mockito.mock(Environment.class));
    runner = spy(temp);

  }

  @After
  public void cleanUp() {
    TestUtils.setField(runner, "mongoTemplate", null);
  }

}
