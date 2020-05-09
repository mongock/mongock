package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.repository;

import com.mongodb.client.MongoCollection;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.entry.ChangeState;
import io.changock.driver.core.entry.ChangeEntryRepository;
import io.changock.migration.api.exception.ChangockException;
import org.bson.Document;

import java.lang.reflect.Field;

public class MongoChangeEntryRepository<CHANGE_ENTRY extends ChangeEntry> extends MongoRepositoryBase<CHANGE_ENTRY> implements ChangeEntryRepository<CHANGE_ENTRY, Document> {

  private static String KEY_EXECUTION_ID;
  private static String KEY_CHANGE_ID;
  private static String KEY_STATE;
  private static String KEY_AUTHOR;

  static {
    try {
       Field field = ChangeEntry.class.getDeclaredField("executionId");
       field.setAccessible(true);
      KEY_EXECUTION_ID = field.getAnnotation(io.changock.utils.field.Field.class).value();

      field = ChangeEntry.class.getDeclaredField("changeId");
      field.setAccessible(true);
      KEY_CHANGE_ID = field.getAnnotation(io.changock.utils.field.Field.class).value();

      field = ChangeEntry.class.getDeclaredField("state");
      field.setAccessible(true);
      KEY_STATE = field.getAnnotation(io.changock.utils.field.Field.class).value();

      field = ChangeEntry.class.getDeclaredField("author");
      field.setAccessible(true);
      KEY_AUTHOR = field.getAnnotation(io.changock.utils.field.Field.class).value();
    } catch (NoSuchFieldException e) {
      throw new ChangockException(e);
    }
  }

  public MongoChangeEntryRepository(MongoCollection<Document> collection) {
    super(collection, new String[]{KEY_EXECUTION_ID, KEY_AUTHOR, KEY_CHANGE_ID});
  }

  @Override
  public boolean isNewChange(String changeSetId, String author) throws ChangockException {
    Document entry = collection.find(buildSearchQueryDBObject(changeSetId, author)).first();
    return entry == null || entry.isEmpty();
  }

  @Override
  public void save(CHANGE_ENTRY changeEntry) throws ChangockException {
    collection.insertOne(toEntity(changeEntry));
  }

  /**
   *
   * @param changeSetId changeSetId
   * @param author author
   * @return search document for changeSetId, author and state==EXECUTED
   */
  protected Document buildSearchQueryDBObject(String changeSetId, String author) {
    return new Document()
        .append(KEY_CHANGE_ID, changeSetId)
        .append(KEY_AUTHOR, author)
        .append(KEY_STATE, ChangeState.EXECUTED.name());
  }
}
