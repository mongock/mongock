package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.test.changelogs.MongockTestResource;
import com.github.fakemongo.Fongo;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Class to provide common configuration for Mongock**Test
 *
 *
 * @since 04/04/2018
 */
public class SpringMongockTestBase {

  static final String CHANGELOG_COLLECTION_NAME = "dbchangelog";

  protected SpringMongock runner;

  protected DB fakeDb;
  protected MongoDatabase fakeMongoDatabase;

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

  public static MongoClient getFakeMongoClient(MongoDatabase fakeMongoDatabase, DB fakeDb) {
    MongoClient mongoClient = mock(MongoClient.class);
    when(mongoClient.getDatabase(anyString())).thenReturn(fakeMongoDatabase);
    when(mongoClient.getDB(anyString())).thenReturn(fakeDb);
    return mongoClient;
  }

  @Before
  public void init() throws Exception {
    fakeDb = new Fongo("testServer").getDB("mongocktest");
    fakeMongoDatabase = new Fongo("testServer").getDatabase("mongocktest");
    com.github.cloudyrock.mongock.TestUtils.setField(changeEntryRepository, "mongoDatabase", fakeMongoDatabase);

    doCallRealMethod().when(changeEntryRepository).save(any(ChangeEntry.class));
    TestUtils.setField(changeEntryRepository, "indexDao", indexDao);
    TestUtils.setField(changeEntryRepository, "changelogCollectionName", CHANGELOG_COLLECTION_NAME);
    TestUtils.setField(changeEntryRepository, "collection", fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME));

    changeService.setChangeLogsBasePackage(MongockTestResource.class.getPackage().getName());
    mongoClient = getFakeMongoClient(fakeMongoDatabase, fakeDb);

    SpringMongock temp = new SpringMongock(
        changeEntryRepository,
        mongoClient,
        changeService,
        lockChecker);

    temp.setChangelogMongoDatabase(fakeMongoDatabase);
    temp.setMongoTemplate(new MongoTemplate(mongoClient, "mongocktest"));
    temp.setEnabled(true);
    temp.setThrowExceptionIfCannotObtainLock(true);
    temp.setSpringEnvironment(null);
    runner = spy(temp);

  }

  @After
  public void cleanUp() throws NoSuchFieldException, IllegalAccessException {
    TestUtils.setField(runner, "mongoTemplate", null);
    fakeDb.dropDatabase();
  }

}
