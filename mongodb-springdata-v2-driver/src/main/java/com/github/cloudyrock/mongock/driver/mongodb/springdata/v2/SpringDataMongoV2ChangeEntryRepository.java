package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2;

import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.core.entry.ChangeEntryRepository;
import com.github.cloudyrock.mongock.driver.mongodb.v3.repository.Mongo3ChangeEntryRepository;
import com.github.cloudyrock.mongock.driver.mongodb.v3.repository.ReadWriteConfiguration;
import com.github.cloudyrock.mongock.exception.MongockException;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class SpringDataMongoV2ChangeEntryRepository<CHANGE_ENTRY extends ChangeEntry> extends Mongo3ChangeEntryRepository<CHANGE_ENTRY> implements ChangeEntryRepository<CHANGE_ENTRY, Document> {

  private final MongoTemplate mongoTemplate;

  public SpringDataMongoV2ChangeEntryRepository(MongoTemplate mongoTemplate, String collectionName, boolean indexCreation) {
    this(mongoTemplate, collectionName, indexCreation, ReadWriteConfiguration.getDefault());
  }

  public SpringDataMongoV2ChangeEntryRepository(MongoTemplate mongoTemplate,
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

  @Override
  public void saveOrUpdate(CHANGE_ENTRY changeEntry) throws MongockException {

    Query filter = new Query().addCriteria(new Criteria()
        .andOperator(
            Criteria.where(KEY_EXECUTION_ID).is(changeEntry.getExecutionId()),
            Criteria.where(KEY_CHANGE_ID).is(changeEntry.getChangeId()),
            Criteria.where(KEY_AUTHOR).is(changeEntry.getAuthor())));
    mongoTemplate.upsert(filter, getUpdateFromEntity(changeEntry), collection.getNamespace().getCollectionName());
  }

  private Update getUpdateFromEntity(CHANGE_ENTRY changeEntry) {
    Update updateChangeEntry = new Update();
    Document entityDocu = toEntity(changeEntry);
    entityDocu.forEach(updateChangeEntry::set);
    return updateChangeEntry;
  }

}
