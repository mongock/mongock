package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.test.changelogs.MongockTestResource;
import com.github.fakemongo.Fongo;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.jongo.Jongo;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Class to provide common configuration for Mongock**Test
 *
 * @author dieppa
 * @since 04/04/2018
 */
public class MongockTestBase {

  static final String CHANGELOG_COLLECTION_NAME = "dbchangelog";

  protected Mongock runner;

  protected Jongo jongo;
  protected DB fakeDb;
  protected MongoDatabase fakeMongoDatabase;

  @Mock
  protected ChangeEntryRepository changeEntryRepository;

  @Mock
  protected LockChecker lockChecker;

  @Mock
  protected LockRepository lockRepository;

  @Spy
  protected ChangeService changeService;

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
    jongo = new Jongo(fakeDb);
    TestUtils.setField(changeEntryRepository, "mongoDatabase", fakeMongoDatabase);

    doCallRealMethod().when(changeEntryRepository).save(any(ChangeEntry.class));
    TestUtils.setField(changeEntryRepository, "indexDao", indexDao);
    TestUtils.setField(changeEntryRepository, "changelogCollectionName", CHANGELOG_COLLECTION_NAME);
    TestUtils.setField(changeEntryRepository, "collection", fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME));
    doNothing().when(changeEntryRepository).verifyDbConnectionAndEnsureIndex();

    changeService.setChangeLogsBasePackage(MongockTestResource.class.getPackage().getName());
    mongoClient = MongockTestBase.getFakeMongoClient(fakeMongoDatabase, fakeDb);

    Mongock temp = new Mongock(
        changeEntryRepository,
        mongoClient,
        changeService,
        lockChecker);

    temp.setChangelogDb(fakeDb);
    temp.setChangelogMongoDatabase(fakeMongoDatabase);
    temp.setChangelogJongo(jongo);
    temp.setChangelogMongoTemplate(new MongoTemplate(mongoClient, "mongocktest"));
    temp.setEnabled(true);
    temp.setThrowExceptionIfCannotObtainLock(true);
    temp.setSpringEnvironment(null);
    runner = spy(temp);

  }

  @After
  public void cleanUp() throws NoSuchFieldException, IllegalAccessException {
    TestUtils.setField(runner, "mongoTemplate", null);
    TestUtils.setField(runner, "jongo", null);
    fakeDb.dropDatabase();
  }

}
