package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2;

import com.github.cloudyrock.mongock.driver.mongodb.v3.repository.Mongo3ChangeEntryRepository;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.core.entry.ChangeEntryRepository;
import io.changock.migration.api.exception.ChangockException;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

public class SpringDataMongo2ChangeEntryRepository<CHANGE_ENTRY extends ChangeEntry> extends Mongo3ChangeEntryRepository<CHANGE_ENTRY> implements ChangeEntryRepository<CHANGE_ENTRY, Document> {

  private final MongoTemplate mongoTemplate;

  public SpringDataMongo2ChangeEntryRepository(MongoTemplate mongoTemplate, String collectionName, boolean indexCreation) {
    super(mongoTemplate.getCollection(collectionName), indexCreation);
    this.mongoTemplate = mongoTemplate;
  }


  @Override
  public void save(CHANGE_ENTRY changeEntry) throws ChangockException {
    mongoTemplate.save(changeEntry, collection.getNamespace().getCollectionName());
  }
}
