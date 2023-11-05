package io.mongock.driver.couchbase.repository;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.PersistTo;
import com.couchbase.client.java.kv.ReplicateTo;
import com.couchbase.client.java.kv.UpsertOptions;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.couchbase.client.java.query.QueryScanConsistency;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.driver.api.entry.ChangeEntryService;
import io.mongock.driver.couchbase.entry.CouchbaseChangeEntry;
import io.mongock.driver.couchbase.util.N1QLQueryProvider;
import io.mongock.utils.field.FieldInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static io.mongock.driver.api.entry.ChangeEntry.KEY_AUTHOR;
import static io.mongock.driver.api.entry.ChangeEntry.KEY_CHANGELOG_CLASS;
import static io.mongock.driver.api.entry.ChangeEntry.KEY_CHANGESET_METHOD;
import static io.mongock.driver.api.entry.ChangeEntry.KEY_CHANGE_ID;
import static io.mongock.driver.api.entry.ChangeEntry.KEY_ERROR_TRACE;
import static io.mongock.driver.api.entry.ChangeEntry.KEY_EXECUTION_HOST_NAME;
import static io.mongock.driver.api.entry.ChangeEntry.KEY_EXECUTION_ID;
import static io.mongock.driver.api.entry.ChangeEntry.KEY_EXECUTION_MILLIS;
import static io.mongock.driver.api.entry.ChangeEntry.KEY_METADATA;
import static io.mongock.driver.api.entry.ChangeEntry.KEY_STATE;
import static io.mongock.driver.api.entry.ChangeEntry.KEY_SYSTEM_CHANGE;
import static io.mongock.driver.api.entry.ChangeEntry.KEY_TIMESTAMP;
import static io.mongock.driver.api.entry.ChangeEntry.KEY_TYPE;

/**
 * Couchbase change entry repository.
 * Uses simple KV operations to store change entries.
 *
 * @author Tigran Babloyan
 */
public class CouchbaseChangeEntryRepository extends CouchbaseRepositoryBase<ChangeEntry> implements ChangeEntryService {
  private final static Logger logger = LoggerFactory.getLogger(CouchbaseChangeEntryRepository.class);
  private final static String DOCUMENT_TYPE_CHANGE_ENTRY = "mongockChangeEntry";

  private static final Set<String> QUERY_FIELDS = new LinkedHashSet<>();

  /**
   * Scan for all fields annotated with {@link io.mongock.utils.field.Field} which are primary
   * and add them to the query fields.
   */
  static {
    // this one is requited to distinguish between change entries and other documents
    QUERY_FIELDS.add(DOCUMENT_TYPE_KEY);
    Arrays.stream(ChangeEntry.class.getDeclaredFields())
        .map(field -> field.getAnnotation(io.mongock.utils.field.Field.class))
        .filter(Objects::nonNull)
        .filter(field -> io.mongock.utils.field.Field.KeyType.PRIMARY.equals(field.type()))
        .forEach(field -> QUERY_FIELDS.add(field.value()));
  }

  private final ChangeEntryKeyGenerator keyGenerator = new ChangeEntryKeyGenerator();

  public CouchbaseChangeEntryRepository(Cluster cluster, Collection collection) {
    super(cluster, collection, QUERY_FIELDS);
  }

  @Override
  public List<ChangeEntry> getEntriesLog() {
    QueryResult result = cluster.query(N1QLQueryProvider.selectAllChangesQuery(collection.bucketName(), collection.scopeName(), collection.name()),
        QueryOptions.queryOptions().parameters(JsonObject.create().put("type", DOCUMENT_TYPE_CHANGE_ENTRY)).scanConsistency(QueryScanConsistency.REQUEST_PLUS));
    return result
        .rowsAsObject()
        .stream()
        .map(CouchbaseChangeEntry::new)
        .collect(Collectors.toList());
  }

  @Override
  public void saveOrUpdate(ChangeEntry changeEntry) throws MongockException {
    String key = keyGenerator.toKey(changeEntry);
    logger.debug("Saving change entry with key {}", key);
    try {
      collection.upsert(key, toEntity(changeEntry),
          UpsertOptions.upsertOptions().durability(PersistTo.ACTIVE, ReplicateTo.NONE));
    } catch (CouchbaseException couchbaseException) {
      logger.warn("Error saving change entry with key {}", key, couchbaseException);
      throw new MongockException(couchbaseException);
    }
  }

  @Override
  public void ensureField(Field field) {
    //  nothing to do for couchbase
  }

  @Override
  public JsonObject toEntity(ChangeEntry domain) {
    JsonObject jsonObject = JsonObject.create();
    addField(jsonObject, KEY_EXECUTION_ID, domain.getExecutionId());
    addField(jsonObject, KEY_CHANGE_ID, domain.getChangeId());
    addField(jsonObject, KEY_AUTHOR, domain.getAuthor());
    addField(jsonObject, KEY_TIMESTAMP, domain.getTimestamp());
    addField(jsonObject, KEY_STATE, domain.getState().toString());
    addField(jsonObject, KEY_TYPE, domain.getType().toString());
    addField(jsonObject, KEY_CHANGELOG_CLASS, domain.getChangeLogClass());
    addField(jsonObject, KEY_CHANGESET_METHOD, domain.getChangeSetMethod());
    addField(jsonObject, KEY_METADATA, domain.getMetadata());
    addField(jsonObject, KEY_EXECUTION_MILLIS, domain.getExecutionMillis());
    addField(jsonObject, KEY_EXECUTION_HOST_NAME, domain.getExecutionHostname());
    addField(jsonObject, KEY_ERROR_TRACE, domain.getErrorTrace().orElse(null));
    addField(jsonObject, KEY_SYSTEM_CHANGE, domain.isSystemChange());
    return jsonObject;

  }

  @Override
  public JsonObject mapFieldInstances(List<FieldInstance> fieldInstanceList) {
    JsonObject document = super.mapFieldInstances(fieldInstanceList);
    document.put(DOCUMENT_TYPE_KEY, DOCUMENT_TYPE_CHANGE_ENTRY);
    return document;
  }
}
