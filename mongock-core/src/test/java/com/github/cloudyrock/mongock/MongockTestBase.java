package com.github.cloudyrock.mongock;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.junit.After;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Class to provide common configuration for Mongock**Test
 *
 *
 * @since 04/04/2018
 */
public class MongockTestBase {

  static final String CHANGELOG_COLLECTION_NAME = "dbchangelog";

  protected Mongock runner;

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
  private MongoRepositoryBase indexDao;

  public static MongoClient getFakeMongoClient(MongoDatabase fakeMongoDatabase) {
    MongoClient mongoClient = mock(MongoClient.class);
    when(mongoClient.getDatabase(anyString())).thenReturn(fakeMongoDatabase);
    return mongoClient;
  }

  public static com.mongodb.client.MongoClient getFakeNewMongoClient(MongoDatabase fakeMongoDatabase) {
    com.mongodb.client.MongoClient mongoClient = mock(com.mongodb.client.MongoClient.class);
    when(mongoClient.getDatabase(anyString())).thenReturn(fakeMongoDatabase);
    return mongoClient;
  }
//
//  @Before
//  public void init() throws Exception {
//    fakeMongoDatabase = new Fongo("testServer").getDatabase("mongocktest");
//    TestUtils.setField(changeEntryRepository, "mongoDatabase", fakeMongoDatabase);
//
//    doCallRealMethod().when(changeEntryRepository).save(any(ChangeEntry.class));
//    TestUtils.setField(changeEntryRepository, "indexDao", indexDao);
//    TestUtils.setField(changeEntryRepository, "changelogCollectionName", CHANGELOG_COLLECTION_NAME);
//    TestUtils.setField(changeEntryRepository, "collection", fakeMongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME));
//
//    changeService.setChangeLogsBasePackage(MongockTestResource.class.getPackage().getName());
//    mongoClient = MongockTestBase.getFakeMongoClient(fakeMongoDatabase);
//
//    Mongock temp = new Mongock(
//        changeEntryRepository,
//        mongoClient,
//        changeService,
//        lockChecker);
//
//    temp.addChangeSetDependency(fakeMongoDatabase);
//    temp.setEnabled(true);
//    temp.setThrowExceptionIfCannotObtainLock(true);
//    runner = spy(temp);
//
//  }

  @After
  public void cleanUp() throws NoSuchFieldException, IllegalAccessException {
    TestUtils.setField(runner, "mongoTemplate", null);
  }

}
