package io.mongock.driver.mongodb.springdata.v3;

import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.driver.api.entry.ExecutedChangeEntry;
import io.mongock.driver.core.entry.ChangeEntryRepositoryWithEntity;
import io.mongock.driver.mongodb.sync.v4.repository.MongoSync4ChangeEntryRepository;
import io.mongock.driver.mongodb.sync.v4.repository.ReadWriteConfiguration;
import io.mongock.api.exception.MongockException;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static io.mongock.driver.api.entry.ChangeState.FAILED;
import static io.mongock.driver.api.entry.ChangeState.IGNORED;
import static io.mongock.driver.api.entry.ChangeState.ROLLBACK_FAILED;

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
            Criteria.where(KEY_EXECUTION_ID).is(changeEntry.getExecutionId()),
            Criteria.where(KEY_CHANGE_ID).is(changeEntry.getChangeId()),
            Criteria.where(KEY_AUTHOR).is(changeEntry.getAuthor())));
    mongoTemplate.upsert(filter, getUpdateFromEntity(changeEntry), collection.getNamespace().getCollectionName());
  }

  @Override
  public List<ChangeEntry> getEntriesLog() {
    return mongoTemplate.findAll(ChangeEntry.class, collectionName);
  }

  private Update getUpdateFromEntity(ChangeEntry changeEntry) {
    Update updateChangeEntry = new Update();
    Document entityDocu = toEntity(changeEntry);
    entityDocu.forEach(updateChangeEntry::set);
    return updateChangeEntry;
  }


  /**
   * TODO THIS WILL E REMOVED ONCE SPRINNGDATA IS DECOPLED from V4
   */
  @Override
  public List<ExecutedChangeEntry> getExecuted() throws MongockException {

    Predicate<ChangeEntry> cleanIrrelevantState = entry -> entry.getState() != IGNORED && entry.getState() != FAILED && entry.getState() != ROLLBACK_FAILED;
    return getEntriesMap()//Maps of List<ChangeEntry>, indexed by changeId
        .values()//collection of List<ChangeEntry>
        .stream()
        .map(duplicatedEntries -> duplicatedEntries.stream().filter(cleanIrrelevantState).collect(Collectors.toList()))//only takes into account executed or rolled back
        .filter(duplicatedEntries -> !duplicatedEntries.isEmpty())
        .map(duplicatedEntries -> duplicatedEntries.get(0))//transform each list in a single ChangeEntry(the first one)
        .sorted(Comparator.comparing(ChangeEntry::getTimestamp))// Sorts the resulting list chronologically
        .filter(ChangeEntry::isExecuted)//only gets the ones that are executed
        .map(ExecutedChangeEntry::new)//transform the entry to an executed entry
        .collect(Collectors.toList());
  }

  /**
   * Retrieves a list of the  entries in database with the current relevant state ordered by execution timestamp.
   *
   * @return list of the  entries in database with the current relevant state ordered by execution timestamp
   * @throws MongockException
   */
  @Override
  public List<ChangeEntry> getAllEntriesWithCurrentState() throws MongockException {
    return getEntriesMap()//Maps of List<ChangeEntry>, indexed by changeId
        .values()//collection of List<ChangeEntry>
        .stream()
        .map(duplicatedEntries -> duplicatedEntries.stream().filter(ChangeEntry::hasRelevantState).collect(Collectors.toList()))//only takes into account relevant states
        .filter(duplicatedEntries -> !duplicatedEntries.isEmpty())
        .map(duplicatedEntries -> duplicatedEntries.get(0))//transform each list in a single ChangeEntry(the first one)
        .sorted(Comparator.comparing(ChangeEntry::getTimestamp))// Sorts the resulting list chronologically
        .collect(Collectors.toList());
  }

  @Override
  public Map<String, List<ChangeEntry>> getEntriesMap() {
    Map<String, List<ChangeEntry>> log = getEntriesLog()
        .stream()
        .collect(Collectors.groupingBy(ChangeEntry::getChangeId));
    log.values().forEach(entries -> entries.sort((c1, c2) -> c2.getTimestamp().compareTo(c1.getTimestamp())));//sorts each list in the map by date in reverse
    return log;
  }



}
