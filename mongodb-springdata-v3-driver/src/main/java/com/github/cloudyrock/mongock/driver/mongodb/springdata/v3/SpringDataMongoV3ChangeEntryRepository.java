package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3;

import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.core.entry.ChangeEntryRepository;
import com.github.cloudyrock.mongock.driver.core.entry.ChangeEntryRepositoryWithEntity;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.repository.MongoSync4ChangeEntryRepository;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.repository.ReadWriteConfiguration;
import com.github.cloudyrock.mongock.exception.MongockException;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class SpringDataMongoV3ChangeEntryRepository<CHANGE_ENTRY extends ChangeEntry> extends MongoSync4ChangeEntryRepository<CHANGE_ENTRY> implements ChangeEntryRepositoryWithEntity<CHANGE_ENTRY, Document> {

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
