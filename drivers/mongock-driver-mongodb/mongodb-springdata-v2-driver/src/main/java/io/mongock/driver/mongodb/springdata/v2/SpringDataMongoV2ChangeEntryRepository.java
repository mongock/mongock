package io.mongock.driver.mongodb.springdata.v2;

import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.driver.core.entry.ChangeEntryRepositoryWithEntity;
import io.mongock.driver.mongodb.v3.repository.Mongo3ChangeEntryRepository;
import io.mongock.driver.mongodb.v3.repository.ReadWriteConfiguration;
import io.mongock.api.exception.MongockException;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class SpringDataMongoV2ChangeEntryRepository<CHANGE_ENTRY extends ChangeEntry> extends Mongo3ChangeEntryRepository<CHANGE_ENTRY> implements ChangeEntryRepositoryWithEntity<CHANGE_ENTRY, Document> {

  private final MongoTemplate mongoTemplate;

  public SpringDataMongoV2ChangeEntryRepository(MongoTemplate mongoTemplate, String collectionName) {
    this(mongoTemplate, collectionName, ReadWriteConfiguration.getDefault());
  }

  public SpringDataMongoV2ChangeEntryRepository(MongoTemplate mongoTemplate,
                                                String collectionName,
                                                ReadWriteConfiguration readWriteConfiguration) {
    super(mongoTemplate.getCollection(collectionName), readWriteConfiguration);
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
