package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.decorator.impl.MongockTemplate;
import com.github.cloudyrock.mongock.decorator.util.MethodInvokerImpl;
import com.github.cloudyrock.mongock.test.changelogs.MongockTestResource;
import com.github.cloudyrock.mongock.utils.IndependentDbIntegrationTestBase;
import com.mongodb.client.MongoDatabase;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.mockito.Matchers.any;
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
public class SpringBootMongockTestBase extends IndependentDbIntegrationTestBase {

  static final String CHANGELOG_COLLECTION_NAME = "dbchangelog";

  protected SpringBootMongock runner;

  protected MongoDatabase mongoDatabase;

  @Mock
  protected ChangeEntryMongoRepository changeEntryRepository;

  @Mock
  protected LockChecker lockChecker;


  @Spy
  protected SpringChangeService changeService;

  @Mock
  private MongoRepositoryBase indexDao;


  @Before
  public final void setUpMockParent() {
    mongoDatabase = mongoClient.getDatabase(DEFAULT_DATABASE_NAME);
    TestUtils.setField(changeEntryRepository, "mongoDatabase", mongoDatabase);

    doCallRealMethod().when(changeEntryRepository).save(any(ChangeEntry.class));
    TestUtils.setField(changeEntryRepository, "indexDao", indexDao);
    TestUtils.setField(changeEntryRepository, "changelogCollectionName", CHANGELOG_COLLECTION_NAME);
    TestUtils.setField(changeEntryRepository, "collection", mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME));

    changeService.setChangeLogsBasePackage(MongockTestResource.class.getPackage().getName());

    SpringBootMongock temp = new SpringBootMongock(
        changeEntryRepository,
        changeService,
        lockChecker);

    ApplicationContext appContextMock = mock(ApplicationContext.class);
    when(appContextMock.getBean(Environment.class)).thenReturn(mock(Environment.class));
    temp.springContext(appContextMock);
    temp.addChangeSetDependency(MongoDatabase.class, mongoDatabase);
    MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "mongocktest");
    temp.addChangeSetDependency(MongoTemplate.class, mongoTemplate);
    temp.addChangeSetDependency(MongockTemplate.class, new MongockTemplate(mongoTemplate, new MethodInvokerImpl(lockChecker)));
    temp.setEnabled(true);
    temp.setThrowExceptionIfCannotObtainLock(true);
    runner = spy(temp);

  }

  @After
  public void cleanUp() {
    TestUtils.setField(runner, "mongoTemplate", null);
  }

}
