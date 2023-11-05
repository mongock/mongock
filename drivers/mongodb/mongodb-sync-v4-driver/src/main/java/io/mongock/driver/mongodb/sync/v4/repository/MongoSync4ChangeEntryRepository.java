package io.mongock.driver.mongodb.sync.v4.repository;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.driver.api.entry.ChangeState;
import io.mongock.driver.api.entry.ChangeType;
import io.mongock.driver.core.entry.ChangeEntryRepositoryWithEntity;
import io.mongock.utils.DateUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.mongock.driver.api.entry.ChangeEntry.KEY_ERROR_TRACE;
import static io.mongock.driver.api.entry.ChangeEntry.KEY_EXECUTION_HOST_NAME;
import static io.mongock.driver.core.lock.LockEntry.EXPIRES_AT_FIELD;
import static io.mongock.driver.core.lock.LockEntry.OWNER_FIELD;
import static io.mongock.driver.core.lock.LockEntry.STATUS_FIELD;

public class MongoSync4ChangeEntryRepository extends MongoSync4RepositoryBase<ChangeEntry> implements ChangeEntryRepositoryWithEntity<Document> {

  private final static Logger logger = LoggerFactory.getLogger(MongoSync4ChangeEntryRepository.class);


  protected static String KEY_EXECUTION_ID;
  protected static String KEY_CHANGE_ID;
  protected static String KEY_STATE;
  protected static String KEY_TYPE;
  protected static String KEY_AUTHOR;
  protected static String KEY_TIMESTAMP;
  protected static String KEY_CHANGELOG_CLASS;
  protected static String KEY_CHANGESET_METHOD;
  protected static String KEY_EXECUTION_MILLIS;
  protected static String KEY_EXECUTION_HOSTNAME;
  protected static String KEY_METADATA;
  protected static String KEY_SYSTEM_CHANGE;

  private ClientSession clientSession;

  static {
    try {
      Field field = ChangeEntry.class.getDeclaredField("executionId");
      field.setAccessible(true);
      KEY_EXECUTION_ID = field.getAnnotation(io.mongock.utils.field.Field.class).value();

      field = ChangeEntry.class.getDeclaredField("changeId");
      field.setAccessible(true);
      KEY_CHANGE_ID = field.getAnnotation(io.mongock.utils.field.Field.class).value();

      field = ChangeEntry.class.getDeclaredField("state");
      field.setAccessible(true);
      KEY_STATE = field.getAnnotation(io.mongock.utils.field.Field.class).value();

      field = ChangeEntry.class.getDeclaredField("type");
      field.setAccessible(true);
      KEY_TYPE = field.getAnnotation(io.mongock.utils.field.Field.class).value();

      field = ChangeEntry.class.getDeclaredField("author");
      field.setAccessible(true);
      KEY_AUTHOR = field.getAnnotation(io.mongock.utils.field.Field.class).value();

      field = ChangeEntry.class.getDeclaredField("timestamp");
      field.setAccessible(true);
      KEY_TIMESTAMP = field.getAnnotation(io.mongock.utils.field.Field.class).value();

      field = ChangeEntry.class.getDeclaredField("changeLogClass");
      field.setAccessible(true);
      KEY_CHANGELOG_CLASS = field.getAnnotation(io.mongock.utils.field.Field.class).value();

      field = ChangeEntry.class.getDeclaredField("changeSetMethod");
      field.setAccessible(true);
      KEY_CHANGESET_METHOD = field.getAnnotation(io.mongock.utils.field.Field.class).value();

      field = ChangeEntry.class.getDeclaredField("metadata");
      field.setAccessible(true);
      KEY_METADATA = field.getAnnotation(io.mongock.utils.field.Field.class).value();

      field = ChangeEntry.class.getDeclaredField("executionMillis");
      field.setAccessible(true);
      KEY_EXECUTION_MILLIS = field.getAnnotation(io.mongock.utils.field.Field.class).value();

      field = ChangeEntry.class.getDeclaredField("executionHostname");
      field.setAccessible(true);
      KEY_EXECUTION_HOSTNAME = field.getAnnotation(io.mongock.utils.field.Field.class).value();

      field = ChangeEntry.class.getDeclaredField("systemChange");
      field.setAccessible(true);
      KEY_SYSTEM_CHANGE = field.getAnnotation(io.mongock.utils.field.Field.class).value();
    } catch (NoSuchFieldException e) {
      throw new MongockException(e);
    }
  }

  public MongoSync4ChangeEntryRepository(MongoCollection<Document> collection) {
    this(collection, ReadWriteConfiguration.getDefault());
  }

  public MongoSync4ChangeEntryRepository(MongoCollection<Document> collection, ReadWriteConfiguration readWriteConfiguration) {
    super(collection, new String[]{KEY_EXECUTION_ID, KEY_AUTHOR, KEY_CHANGE_ID}, readWriteConfiguration);
  }

  @Override
  public List<ChangeEntry> getEntriesLog() {
    return collection.find()
        .into(new ArrayList<>())
        .stream()
        .map(entry -> new ChangeEntry(
            entry.getString(KEY_EXECUTION_ID),
            entry.getString(KEY_CHANGE_ID),
            entry.getString(KEY_AUTHOR),
            DateUtils.toDate(entry.get(KEY_TIMESTAMP)),
            entry.containsKey(KEY_STATE) ? ChangeState.valueOf(entry.getString(KEY_STATE)) : null,
            entry.containsKey(KEY_TYPE) ? ChangeType.valueOf(entry.getString(KEY_TYPE)) : null,
            entry.getString(KEY_CHANGELOG_CLASS),
            entry.getString(KEY_CHANGESET_METHOD),
            entry.containsKey(KEY_EXECUTION_MILLIS) && entry.get(KEY_EXECUTION_MILLIS) != null
                ? ((Number) entry.get(KEY_EXECUTION_MILLIS)).longValue() : -1L,
            entry.getString(KEY_EXECUTION_HOSTNAME),
            entry.get(KEY_METADATA),
            entry.getBoolean(KEY_SYSTEM_CHANGE)))
        .collect(Collectors.toList());
  }

  @Override
  public void saveOrUpdate(ChangeEntry changeEntry) throws MongockException {
    Bson filter = Filters.and(
        Filters.eq(KEY_EXECUTION_ID, changeEntry.getExecutionId()),
        Filters.eq(KEY_CHANGE_ID, changeEntry.getChangeId()),
        Filters.eq(KEY_AUTHOR, changeEntry.getAuthor())
    );

    Document entryDocument = toEntity(changeEntry);
    UpdateResult result = getClientSession()
        .map(clientSession -> collection.replaceOne(clientSession, filter, entryDocument, new ReplaceOptions().upsert(true)))
        .orElseGet(() -> collection.replaceOne(filter, entryDocument, new ReplaceOptions().upsert(true)));
    logger.debug("SaveOrUpdate[{}] with result" +
        "\n[upsertId:{}, matches: {}, modifies: {}, acknowledged: {}]", changeEntry, result.getUpsertedId(), result.getMatchedCount(), result.getModifiedCount(), result.wasAcknowledged());
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

  @Override
  public void ensureField(Field field) {
    // Nothing to do in MongoDB
  }


  @Override
  public Document toEntity(ChangeEntry domain) {
    return new Document()
        .append(KEY_EXECUTION_ID, domain.getExecutionId())
        .append(KEY_CHANGE_ID, domain.getChangeId())
        .append(KEY_AUTHOR, domain.getAuthor())
        .append(KEY_TIMESTAMP, domain.getTimestamp())
        .append(KEY_STATE, domain.getState().toString())
        .append(KEY_TYPE, domain.getType().toString())
        .append(KEY_CHANGELOG_CLASS, domain.getChangeLogClass())
        .append(KEY_CHANGESET_METHOD, domain.getChangeSetMethod())
        .append(KEY_METADATA, domain.getMetadata())
        .append(KEY_EXECUTION_MILLIS, domain.getExecutionMillis())
        .append(KEY_EXECUTION_HOST_NAME, domain.getExecutionHostname())
        .append(KEY_ERROR_TRACE, domain.getErrorTrace().orElse(null))
        .append(KEY_SYSTEM_CHANGE, domain.isSystemChange());
  }
}
