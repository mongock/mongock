package io.mongock.driver.mongodb.sync.v4.repository;

import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.driver.api.entry.ChangeState;
import io.mongock.driver.api.entry.ExecutedChangeEntry;
import io.mongock.driver.core.entry.ChangeEntryRepositoryWithEntity;
import io.mongock.api.exception.MongockException;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import io.mongock.utils.field.ChangeEntryFields;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.result.InsertOneResult;
import io.mongock.driver.api.entry.ChangeType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MongoSync4ChangeEntryRepository extends MongoSync4RepositoryBase<ChangeEntry> implements ChangeEntryRepositoryWithEntity<Document> {

  private ClientSession clientSession;


  public MongoSync4ChangeEntryRepository(MongoCollection<Document> collection) {
    this(collection, ReadWriteConfiguration.getDefault());
  }

  public MongoSync4ChangeEntryRepository(MongoCollection<Document> collection, ReadWriteConfiguration readWriteConfiguration) {
    super(collection, new String[]{ChangeEntryFields.KEY_EXECUTION_ID, ChangeEntryFields.KEY_AUTHOR, ChangeEntryFields.KEY_CHANGE_ID}, readWriteConfiguration);
  }

  @Override
  public boolean isAlreadyExecuted(String changeSetId, String author) throws MongockException {
    Document entry = collection.find(buildSearchQueryDBObject(changeSetId, author)).sort(Sorts.descending(ChangeEntryFields.KEY_TIMESTAMP)).first();    
    if (entry != null && !entry.isEmpty()) {
      String entryState = entry.getString(ChangeEntryFields.KEY_STATE);
      return entryState == null ||
             entryState.isEmpty() ||
             entryState.equals(ChangeState.EXECUTED.name());
    }
    else {
      return false;
    }
  }
  
  @Override
  public List<ExecutedChangeEntry> getExecuted() throws MongockException {
    Bson matchExecutedOrRolledBack = Aggregates.match(Filters.or(
        Filters.eq(ChangeEntryFields.KEY_STATE, ChangeState.EXECUTED.name()),
        Filters.eq(ChangeEntryFields.KEY_STATE, ChangeState.ROLLED_BACK.name()),
        Filters.eq(ChangeEntryFields.KEY_STATE, null),
        Filters.exists(ChangeEntryFields.KEY_STATE, false)
    ));
    Bson previousSort = Aggregates.sort(Sorts.descending(ChangeEntryFields.KEY_TIMESTAMP));
    Bson group = Aggregates.group(Projections.fields(Filters.eq(ChangeEntryFields.KEY_CHANGE_ID, "$" + ChangeEntryFields.KEY_CHANGE_ID), Filters.eq(ChangeEntryFields.KEY_AUTHOR, "$" + ChangeEntryFields.KEY_AUTHOR)),
                                  Accumulators.first(ChangeEntryFields.KEY_CHANGE_ID, "$" + ChangeEntryFields.KEY_CHANGE_ID),
                                  Accumulators.first(ChangeEntryFields.KEY_AUTHOR, "$" + ChangeEntryFields.KEY_AUTHOR),
                                  Accumulators.first(ChangeEntryFields.KEY_STATE, "$" + ChangeEntryFields.KEY_STATE),
                                  Accumulators.first(ChangeEntryFields.KEY_TIMESTAMP, "$" + ChangeEntryFields.KEY_TIMESTAMP),
                                  Accumulators.first(ChangeEntryFields.KEY_CHANGELOG_CLASS, "$" + ChangeEntryFields.KEY_CHANGELOG_CLASS),
                                  Accumulators.first(ChangeEntryFields.KEY_CHANGESET_METHOD, "$" + ChangeEntryFields.KEY_CHANGESET_METHOD));
    Bson projection = Aggregates.project(Projections.fields(Projections.include(ChangeEntryFields.KEY_CHANGE_ID, ChangeEntryFields.KEY_AUTHOR, ChangeEntryFields.KEY_STATE, ChangeEntryFields.KEY_TIMESTAMP, ChangeEntryFields.KEY_CHANGELOG_CLASS, ChangeEntryFields.KEY_CHANGESET_METHOD), Projections.excludeId()));
    Bson finalSort = Aggregates.sort(Sorts.ascending(ChangeEntryFields.KEY_TIMESTAMP));
    Bson matchOnlyExecuted = Aggregates.match(Filters.or(
        Filters.eq(ChangeEntryFields.KEY_STATE, ChangeState.EXECUTED.name()),
        Filters.eq(ChangeEntryFields.KEY_STATE, null),
        Filters.exists(ChangeEntryFields.KEY_STATE, false)
    ));
    
    return collection.aggregate(Arrays.asList(matchExecutedOrRolledBack, previousSort, group, projection, matchOnlyExecuted, finalSort))
                     .into(new ArrayList<>())
                     .stream()
                     .map(entry -> new ExecutedChangeEntry(entry.getString(ChangeEntryFields.KEY_CHANGE_ID),
                                                           entry.getString(ChangeEntryFields.KEY_AUTHOR),
                                                           entry.getDate(ChangeEntryFields.KEY_TIMESTAMP),
                                                           entry.getString(ChangeEntryFields.KEY_CHANGELOG_CLASS),
                                                           entry.getString(ChangeEntryFields.KEY_CHANGESET_METHOD)))
                     .collect(Collectors.toList());
  }

  @Override
  public List<ChangeEntry> getEntriesLog() {
    return collection.find()
            .into(new ArrayList<>())
            .stream()
            .map(entry -> new ChangeEntry(
                                  entry.getString(ChangeEntryFields.KEY_EXECUTION_ID),
                                  entry.getString(ChangeEntryFields.KEY_CHANGE_ID),
                                  entry.getString(ChangeEntryFields.KEY_AUTHOR),
                                  entry.getDate(ChangeEntryFields.KEY_TIMESTAMP),
                                  entry.getString(ChangeEntryFields.KEY_STATE) != null 
                                          ? ChangeState.valueOf(entry.getString(ChangeEntryFields.KEY_STATE)) 
                                          : null,
                                  entry.getString(ChangeEntryFields.KEY_TYPE) != null 
                                          ? ChangeType.valueOf(entry.getString(ChangeEntryFields.KEY_TYPE)) 
                                          : null,
                                  entry.getString(ChangeEntryFields.KEY_CHANGELOG_CLASS),
                                  entry.getString(ChangeEntryFields.KEY_CHANGESET_METHOD),
                                  entry.getLong(ChangeEntryFields.KEY_EXECUTION_MILLIS),
                                  entry.getString(ChangeEntryFields.KEY_EXECUTION_HOSTNAME),
                                  entry.get(ChangeEntryFields.KEY_METADATA)))
            .collect(Collectors.toList());
  }

  @Override
  public void saveOrUpdate(ChangeEntry changeEntry) throws MongockException {
    Bson filter = Filters.and(
        Filters.eq(ChangeEntryFields.KEY_EXECUTION_ID, changeEntry.getExecutionId()),
        Filters.eq(ChangeEntryFields.KEY_CHANGE_ID, changeEntry.getChangeId()),
        Filters.eq(ChangeEntryFields.KEY_AUTHOR, changeEntry.getAuthor())
    );

    Document document = collection.find(filter).first();
    if (document != null) {
      toEntity(changeEntry).forEach(document::put);
      UpdateResult result = getClientSession()
          .map(clientSession -> collection.updateOne(clientSession, filter, new Document("$set", document), new UpdateOptions().upsert(true)))
          .orElseGet(() -> collection.updateOne(filter, new Document("$set", document), new UpdateOptions().upsert(true)));
    } else {
      InsertOneResult result = getClientSession()
          .map(clientSession -> collection.insertOne(clientSession, toEntity(changeEntry)))
          .orElseGet(() -> collection.insertOne(toEntity(changeEntry)));
    }
  }


  public void setClientSession(ClientSession clientSession) {
    this.clientSession = clientSession;
  }

  public void clearClientSession() {
    setClientSession(null);
  }

  private Optional<ClientSession> getClientSession() {
    return Optional.ofNullable(clientSession);
  }


  /**
   * Check if a changeSet with given changeSetId and author and
   * (state == EXECUTED OR state == ROLLED_BACK OR state == null OR estate doesn't exists)
   * @param changeSetId changeSetId
   * @param author author
   * @return query filter object
   */
  protected Bson buildSearchQueryDBObject(String changeSetId, String author) {
    Bson executedStateOrNoExisting = Filters.or(
        Filters.eq(ChangeEntryFields.KEY_STATE, ChangeState.EXECUTED.name()),
        Filters.eq(ChangeEntryFields.KEY_STATE, ChangeState.ROLLED_BACK.name()),
        Filters.eq(ChangeEntryFields.KEY_STATE, null),
        Filters.exists(ChangeEntryFields.KEY_STATE, false)
    );
    return Filters.and(
        Filters.eq(ChangeEntryFields.KEY_CHANGE_ID, changeSetId),
        Filters.eq(ChangeEntryFields.KEY_AUTHOR, author),
        executedStateOrNoExisting
    );
  }

}
