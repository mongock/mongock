package io.mongock.driver.mongodb.springdata.v2.config;


import com.mongodb.ReadConcernLevel;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties("mongock.mongo-db")
public class MongoDBConfiguration {

  private String myValue;

  private WriteConcernLevel writeConcern = WriteConcernLevel.MAJORITY_WITH_JOURNAL;

  private ReadConcernLevel readConcern = ReadConcernLevel.MAJORITY;

  private ReadPreferenceLevel readPreference = ReadPreferenceLevel.PRIMARY;


  public String getMyValue() {
    return myValue;
  }

  public void setMyValue(String myValue) {
    this.myValue = myValue;
  }

  public WriteConcernLevel getWriteConcern() {
    return writeConcern;
  }

  public void setWriteConcern(WriteConcernLevel writeConcern) {
    this.writeConcern = writeConcern;
  }

  public ReadConcernLevel getReadConcern() {
    return readConcern;
  }

  public void setReadConcern(ReadConcernLevel readConcern) {
    this.readConcern = readConcern;
  }

  public ReadPreferenceLevel getReadPreference() {
    return readPreference;
  }

  public void setReadPreference(ReadPreferenceLevel readPreference) {
    this.readPreference = readPreference;
  }

  protected WriteConcern getBuiltMongoDBWriteConcern() {
    WriteConcern wc = new WriteConcern(writeConcern.w).withJournal(writeConcern.journal);
    return writeConcern.getwTimeoutMs() == null
        ? wc
        : wc.withWTimeout(writeConcern.getwTimeoutMs().longValue(), TimeUnit.MILLISECONDS);
  }


  public static class WriteConcernLevel {

    private String w;
    private Integer wTimeoutMs;
    private Boolean journal;

    public static final WriteConcernLevel MAJORITY_WITH_JOURNAL =  new WriteConcernLevel(
        WriteConcern.MAJORITY.getWString(),
        null,
        true);


    public WriteConcernLevel(String w, Integer wTimeoutMs, Boolean journal) {
      this.w = w;
      this.wTimeoutMs = wTimeoutMs;
      this.journal = journal;
    }

    public String getW() {
      return w;
    }

    public void setW(String w) {
      this.w = w;
    }

    public Integer getwTimeoutMs() {
      return wTimeoutMs;
    }

    public void setwTimeoutMs(Integer wTimeoutMs) {
      this.wTimeoutMs = wTimeoutMs;
    }

    public Boolean isJournal() {
      return journal;
    }

    public void setJournal(Boolean journal) {
      this.journal = journal;
    }
  }

  public enum ReadPreferenceLevel {
    PRIMARY(ReadPreference.primary()),
    PRIMARY_PREFERRED(ReadPreference.primaryPreferred()),
    SECONDARY(ReadPreference.secondary()),
    SECONDARY_PREFERRED(ReadPreference.secondaryPreferred()),
    NEAREST(ReadPreference.nearest());

    private ReadPreference value;

    ReadPreferenceLevel(ReadPreference value) {
      this.value = value;
    }

    public ReadPreference getValue() {
      return value;
    }
  }
}
