package com.github.cloudyrock.mongock.driver.mongodb.v3.repository;


import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;

public class ReadWriteConfiguration {
  private final WriteConcern writeConcern;
  private final ReadConcern readConcern;
  private final ReadPreference readPreference;


  public static ReadWriteConfiguration getDefault() {
    return new ReadWriteConfiguration(
        WriteConcern.MAJORITY.withJournal(true), ReadConcern.MAJORITY,ReadPreference.primary()
    );
  }

  public ReadWriteConfiguration(WriteConcern writeConcern, ReadConcern readConcern, ReadPreference readPreference) {
    this.writeConcern = writeConcern;
    this.readConcern = readConcern;
    this.readPreference = readPreference;
  }


  public WriteConcern getWriteConcern() {
    return writeConcern;
  }

  public ReadConcern getReadConcern() {
    return readConcern;
  }

  public ReadPreference getReadPreference() {
    return readPreference;
  }
}

