package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.repository;

import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeState;
import com.github.cloudyrock.mongock.driver.core.entry.ChangeEntryRepository;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.lang.reflect.Field;

public class MongoSync4ChangeEntryRepository<CHANGE_ENTRY extends ChangeEntry> extends MongoSync4RepositoryBase<CHANGE_ENTRY> implements ChangeEntryRepository<CHANGE_ENTRY, Document> {

  private static String KEY_EXECUTION_ID;
  private static String KEY_CHANGE_ID;
  private static String KEY_STATE;
  private static String KEY_AUTHOR;

  static {
    try {
       Field field = ChangeEntry.class.getDeclaredField("executionId");
       field.setAccessible(true);
      KEY_EXECUTION_ID = field.getAnnotation(com.github.cloudyrock.mongock.utils.field.Field.class).value();

      field = ChangeEntry.class.getDeclaredField("changeId");
      field.setAccessible(true);
      KEY_CHANGE_ID = field.getAnnotation(com.github.cloudyrock.mongock.utils.field.Field.class).value();

      field = ChangeEntry.class.getDeclaredField("state");
      field.setAccessible(true);
      KEY_STATE = field.getAnnotation(com.github.cloudyrock.mongock.utils.field.Field.class).value();

      field = ChangeEntry.class.getDeclaredField("author");
      field.setAccessible(true);
      KEY_AUTHOR = field.getAnnotation(com.github.cloudyrock.mongock.utils.field.Field.class).value();
    } catch (NoSuchFieldException e) {
      throw new MongockException(e);
    }
  }

  public MongoSync4ChangeEntryRepository(MongoCollection<Document> collection, boolean indexCreation) {
    super(collection, new String[]{KEY_EXECUTION_ID, KEY_AUTHOR, KEY_CHANGE_ID}, indexCreation);
  }

  @Override
  public boolean isAlreadyExecuted(String changeSetId, String author) throws MongockException {
    Document entry = collection.find(buildSearchQueryDBObject(changeSetId, author)).first();
    return entry != null && !entry.isEmpty();
  }

  @Override
  public void save(CHANGE_ENTRY changeEntry) throws MongockException {
    collection.insertOne(toEntity(changeEntry));
  }


  /**
   * Check if a changeSet with given changeSetId and author and
   * (state == EXECUTED OR state == null OR estate doesn't exists)
   * @param changeSetId changeSetId
   * @param author author
   * @return true if a changeSet with given changeSetId and author is already executed, false otherwise
   */
  protected Bson buildSearchQueryDBObject(String changeSetId, String author) {
    Bson executedStateOrNoExisting = Filters.or(
        Filters.eq(KEY_STATE, ChangeState.EXECUTED.name()),
        Filters.eq(KEY_STATE, null),
        Filters.exists(KEY_STATE, false)
    );
    return Filters.and(
        Filters.eq(KEY_CHANGE_ID, changeSetId),
        Filters.eq(KEY_AUTHOR, author),
        executedStateOrNoExisting
    );
  }
}
