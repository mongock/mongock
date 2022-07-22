package io.mongock.driver.mongodb.springdata.v3;


import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.mongock.api.config.MongockConfiguration;
import io.mongock.driver.mongodb.springdata.v3.config.MongoDBConfiguration;
import io.mongock.driver.mongodb.springdata.v3.config.SpringDataMongoV3Context;
import io.mongock.driver.mongodb.sync.v4.repository.MongoSync4RepositoryBase;
import io.mongock.driver.mongodb.sync.v4.repository.util.RepositoryAccessorHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Optional;

public class SpringDataMongoV3ContextTest {


  /**
   * public ConnectionDriver connectionDriver(MongoTemplate mongoTemplate,
   *                                            SpringMongoDBConfiguration config,
   *                                            Optional<PlatformTransactionManager> txManagerOpt)
   */



  @Test
  @Disabled("Find the way to mock it  ")
  public void shouldCreateDefaultReadWriteConcerns_whenCreating_ifNoParams() {
    MongoTemplate mongoTemplate = Mockito.mock(MongoTemplate.class);
    Mockito.when(mongoTemplate.getDb()).thenReturn(Mockito.mock(MongoDatabase.class));
    Optional<PlatformTransactionManager> opt = Optional.empty();

    MongockConfiguration config = Mockito.spy(new MongockConfiguration());
    MongoDBConfiguration mongoDbConfig = Mockito.spy(new MongoDBConfiguration());
//    Mockito.when(config.getMongoDb()).thenReturn(new SpringMongoDBConfiguration.MongoDBConfiguration())
//    SpringMongoDBConfiguration.MongoDBConfiguration mongoDBConfig = new SpringMongoDBConfiguration.MongoDBConfiguration();

    SpringDataMongoV3Driver driver = (SpringDataMongoV3Driver)new SpringDataMongoV3Context().connectionDriver(
        mongoTemplate, config, mongoDbConfig, opt
    );
    WriteConcern expectedWriteConcern = WriteConcern.MAJORITY;
    ReadConcern expectedReadConcern = ReadConcern.MAJORITY;
    ReadPreference expectedReadPreference = ReadPreference.primary();

    MongoCollection changeEntryCollection = RepositoryAccessorHelper.getCollection((MongoSync4RepositoryBase) driver.getChangeEntryService());
    Assertions.assertEquals(expectedWriteConcern, changeEntryCollection.getWriteConcern());
    Assertions.assertEquals(expectedReadConcern, changeEntryCollection.getReadConcern());
    Assertions.assertEquals(expectedReadPreference, changeEntryCollection.getReadPreference());
  }

}
