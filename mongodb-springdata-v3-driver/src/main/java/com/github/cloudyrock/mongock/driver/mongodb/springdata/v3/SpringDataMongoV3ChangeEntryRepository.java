package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3;

import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.core.entry.ChangeEntryRepository;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.repository.MongoSync4ChangeEntryRepository;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.repository.ReadWriteConfiguration;
import com.github.cloudyrock.mongock.exception.MongockException;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

public class SpringDataMongoV3ChangeEntryRepository<CHANGE_ENTRY extends ChangeEntry> extends MongoSync4ChangeEntryRepository<CHANGE_ENTRY> implements ChangeEntryRepository<CHANGE_ENTRY, Document> {

  private final MongoTemplate mongoTemplate;

  public SpringDataMongoV3ChangeEntryRepository(MongoTemplate mongoTemplate, String collectionName, boolean indexCreation) {
    this(mongoTemplate, collectionName, indexCreation, ReadWriteConfiguration.getDefault());
  }

  public SpringDataMongoV3ChangeEntryRepository(MongoTemplate mongoTemplate,
                                                String collectionName,
                                                boolean indexCreation,
                                                ReadWriteConfiguration readWriteConfiguration) {
    super(mongoTemplate.getCollection(collectionName), indexCreation, readWriteConfiguration);
    this.mongoTemplate = mongoTemplate;
  }


  @Override
  public void save(CHANGE_ENTRY changeEntry) throws MongockException {
    mongoTemplate.save(changeEntry, collection.getNamespace().getCollectionName());
  }

}
