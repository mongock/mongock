package io.mongock.driver.mongodb.springdata.v3.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import com.mongodb.WriteConcern;

public class MongoDBConfigurationTest {

  @Test
  public void writeConcern_defaultValuesTest() {
    MongoDBConfiguration mongoConfig = new MongoDBConfiguration();

    WriteConcern actual = mongoConfig.getBuiltMongoDBWriteConcern();
    WriteConcern expected = WriteConcern.MAJORITY.withJournal(true);

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void writeConcern_majorityWithTimeoutAndJournalFalseTest() {
    MongoDBConfiguration mongoConfig = new MongoDBConfiguration();
    mongoConfig.setWriteConcern(
        new MongoDBConfiguration.WriteConcernLevel("majority", 1000, false)
    );

    WriteConcern actual = mongoConfig.getBuiltMongoDBWriteConcern();
    WriteConcern expected = WriteConcern.MAJORITY
        .withWTimeout(1000, TimeUnit.MILLISECONDS)
        .withJournal(false);

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void writeConcern_w1WithTimeoutJournalTrueTest() {
    MongoDBConfiguration mongoConfig = new MongoDBConfiguration();
    mongoConfig.setWriteConcern(
        new MongoDBConfiguration.WriteConcernLevel("1", 2000, true)
    );

    WriteConcern actual = mongoConfig.getBuiltMongoDBWriteConcern();
    WriteConcern expected = WriteConcern.W1
        .withWTimeout(2000, TimeUnit.MILLISECONDS)
        .withJournal(true);

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void writeConcern_replicaSetTagNameTest() {
    MongoDBConfiguration mongoConfig = new MongoDBConfiguration();
    mongoConfig.setWriteConcern(
        new MongoDBConfiguration.WriteConcernLevel("12-TAG-34", null, null)
    );

    WriteConcern actual = mongoConfig.getBuiltMongoDBWriteConcern();
    WriteConcern expected = new WriteConcern("12-TAG-34");

    assertThat(actual).isEqualTo(expected);
  }

}
