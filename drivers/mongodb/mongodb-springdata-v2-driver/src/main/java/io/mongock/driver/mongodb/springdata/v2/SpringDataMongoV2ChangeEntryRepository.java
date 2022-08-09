package io.mongock.driver.mongodb.springdata.v2;

import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.driver.core.entry.ChangeEntryRepositoryWithEntity;
import io.mongock.driver.mongodb.v3.repository.Mongo3ChangeEntryRepository;
import io.mongock.driver.mongodb.v3.repository.ReadWriteConfiguration;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class SpringDataMongoV2ChangeEntryRepository extends Mongo3ChangeEntryRepository implements ChangeEntryRepositoryWithEntity<Document> {

  private final MongoOperations mongoOperations;
  private final boolean transactionable;

  public SpringDataMongoV2ChangeEntryRepository(MongoOperations mongoOperations,
                                                 String collectionName,
                                                 ReadWriteConfiguration readWriteConfiguration,
                                                 boolean transactionable) {
    super(mongoOperations.getCollection(collectionName), readWriteConfiguration);
    this.mongoOperations = mongoOperations;
    this.transactionable = transactionable;
  }

  @Override
  public void saveOrUpdate(ChangeEntry changeEntry) throws MongockException {

    if (transactionable) {
      //if transaction is enabled, it will delegate the write concern to the transactionManager
      Query filter = new Query().addCriteria(new Criteria()
          .andOperator(
              Criteria.where(KEY_EXECUTION_ID).is(changeEntry.getExecutionId()),
              Criteria.where(KEY_CHANGE_ID).is(changeEntry.getChangeId()),
              Criteria.where(KEY_AUTHOR).is(changeEntry.getAuthor())));
      mongoOperations.upsert(filter, getUpdateFromEntity(changeEntry), collection.getNamespace().getCollectionName());
    } else {
      super.saveOrUpdate(changeEntry);
    }
  }

  private Update getUpdateFromEntity(ChangeEntry changeEntry) {
    Update updateChangeEntry = new Update();
    toEntity(changeEntry).forEach(updateChangeEntry::set);
    return updateChangeEntry;
  }

}
