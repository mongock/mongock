package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.test.changelogs.MongockTestResource;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.context.ApplicationContext;
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
public class SpringBootMongockTestBase {

  static final String CHANGELOG_COLLECTION_NAME = "dbchangelog";

  protected SpringBootMongock runner;

  protected MongoDatabase mongoDatabase;

  @Mock
  protected ChangeEntryRepository changeEntryRepository;

  @Mock
  protected LockChecker lockChecker;

  @Mock
  protected LockRepository lockRepository;

  @Spy
  protected SpringChangeService changeService;

  protected MongoClient mongoClient;

  @Mock
  private MongoRepository indexDao;


  protected static final String MONGO_CONTAINER = "mongo:3.1.5";
  protected static final Integer MONGO_PORT = 27017;
  protected static final String DEFAULT_DATABASE_NAME = "mongocktest";

  @Rule
  public GenericContainer mongo = new GenericContainer(MONGO_CONTAINER).withExposedPorts(MONGO_PORT);

  @Before
  public final void setUpParent() {
    mongoClient = new MongoClient(mongo.getContainerIpAddress(), mongo.getFirstMappedPort());
    mongoDatabase = mongoClient.getDatabase(DEFAULT_DATABASE_NAME);
    TestUtils.setField(changeEntryRepository, "mongoDatabase", mongoDatabase);

    doCallRealMethod().when(changeEntryRepository).save(any(ChangeEntry.class));
    TestUtils.setField(changeEntryRepository, "indexDao", indexDao);
    TestUtils.setField(changeEntryRepository, "changelogCollectionName", CHANGELOG_COLLECTION_NAME);
    TestUtils.setField(changeEntryRepository, "collection", mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME));

    changeService.setChangeLogsBasePackage(MongockTestResource.class.getPackage().getName());

    SpringBootMongock temp = new SpringBootMongock(
        changeEntryRepository,
        mongoClient,
        changeService,
        lockChecker);

    temp.springContext(mock(ApplicationContext.class));
    temp.setChangelogMongoDatabase(mongoDatabase);
    temp.setMongoTemplate(new MongoTemplate(mongoClient, "mongocktest"));
    temp.setEnabled(true);
    temp.setThrowExceptionIfCannotObtainLock(true);
    runner = spy(temp);

  }

  @After
  public void cleanUp() {
    TestUtils.setField(runner, "mongoTemplate", null);
  }

}
