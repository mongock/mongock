package io.mongock.driver.mongodb.springdata.v3;

import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.driver.core.entry.ChangeEntryRepositoryWithEntity;
import io.mongock.driver.mongodb.sync.v4.repository.MongoSync4ChangeEntryRepository;
import io.mongock.driver.mongodb.sync.v4.repository.ReadWriteConfiguration;
import io.mongock.api.exception.MongockException;
import io.mongock.utils.field.ChangeEntryFields;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public class SpringDataMongoV3ChangeEntryRepository extends MongoSync4ChangeEntryRepository implements ChangeEntryRepositoryWithEntity<Document> {

  private final MongoTemplate mongoTemplate;
  private final String collectionName;

  public SpringDataMongoV3ChangeEntryRepository(MongoTemplate mongoTemplate, String collectionName) {
    this(mongoTemplate, collectionName, ReadWriteConfiguration.getDefault());
  }

  public SpringDataMongoV3ChangeEntryRepository(MongoTemplate mongoTemplate, String collectionName, ReadWriteConfiguration readWriteConfiguration) {
    super(mongoTemplate.getCollection(collectionName), readWriteConfiguration);
    this.mongoTemplate = mongoTemplate;
    this.collectionName = collectionName;
  }


  @Override
  public void saveOrUpdate(ChangeEntry changeEntry) throws MongockException {

    Query filter = new Query().addCriteria(new Criteria()
        .andOperator(
            Criteria.where(ChangeEntryFields.KEY_EXECUTION_ID).is(changeEntry.getExecutionId()),
            Criteria.where(ChangeEntryFields.KEY_CHANGE_ID).is(changeEntry.getChangeId()),
            Criteria.where(ChangeEntryFields.KEY_AUTHOR).is(changeEntry.getAuthor())));
    mongoTemplate.upsert(filter, getUpdateFromEntity(changeEntry), collection.getNamespace().getCollectionName());
  }


//  @Override
//  public List<ChangeEntry> getEntriesLog() {
//    return mongoTemplate.findAll(ChangeEntry.class, collectionName);
//  }

  private Update getUpdateFromEntity(ChangeEntry changeEntry) {
    Update updateChangeEntry = new Update();
    Document entityDocu = toEntity(changeEntry);
    entityDocu.forEach(updateChangeEntry::set);
    return updateChangeEntry;
  }




}
