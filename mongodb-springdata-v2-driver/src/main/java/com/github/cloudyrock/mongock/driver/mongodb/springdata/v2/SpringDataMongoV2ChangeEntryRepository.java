package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2;

import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.core.entry.ChangeEntryRepository;
import com.github.cloudyrock.mongock.driver.mongodb.v3.repository.Mongo3ChangeEntryRepository;
import com.github.cloudyrock.mongock.exception.MongockException;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

public class SpringDataMongoV2ChangeEntryRepository<CHANGE_ENTRY extends ChangeEntry> extends Mongo3ChangeEntryRepository<CHANGE_ENTRY> implements ChangeEntryRepository<CHANGE_ENTRY, Document> {

  private final MongoTemplate mongoTemplate;

  public SpringDataMongoV2ChangeEntryRepository(MongoTemplate mongoTemplate, String collectionName, boolean indexCreation) {
    super(mongoTemplate.getCollection(collectionName), indexCreation);
    this.mongoTemplate = mongoTemplate;
  }


  @Override
  public void save(CHANGE_ENTRY changeEntry) throws MongockException {
    mongoTemplate.save(changeEntry, collection.getNamespace().getCollectionName());
  }
}
