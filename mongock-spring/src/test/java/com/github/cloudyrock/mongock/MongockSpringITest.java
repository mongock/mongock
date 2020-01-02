package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.test.changelogs.MongockTestResource;
import com.github.cloudyrock.mongock.utils.IndependentDbIntegrationTestBase;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class MongockSpringITest extends IndependentDbIntegrationTestBase {

  private static final String CHANGELOG_COLLECTION_NAME = "mongockChangeLog";

  private SpringMongock runner;

  @Before
  public void init()  {
    runner = new SpringMongockBuilder(this.mongoClient, DEFAULT_DATABASE_NAME, MongockTestResource.class.getPackage().getName())
        .setLockQuickConfig()
        .setSpringEnvironment(Mockito.mock(Environment.class))
        .build();

  }

  @Test
  public void shouldExecuteAllChangeSets() {

    // when
    runner.execute();
    runner.execute();

    // then

    // dbchangelog collection checking
    long change1 = this.mongoClient.getDatabase(DEFAULT_DATABASE_NAME).getCollection(CHANGELOG_COLLECTION_NAME).count(new Document()
        .append(ChangeEntry.KEY_CHANGE_ID, "test1")
        .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change1);
  }


}
