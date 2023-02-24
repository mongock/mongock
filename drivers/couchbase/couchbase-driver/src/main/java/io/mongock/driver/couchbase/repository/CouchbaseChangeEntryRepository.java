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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Couchbase change entry repository. 
 * Uses simple KV operations to store change entries.
 *
 * @author Tigran Babloyan
 */
public class CouchbaseChangeEntryRepository extends CouchbaseRepositoryBase<ChangeEntry> implements ChangeEntryService {
  private final static Logger logger = LoggerFactory.getLogger(CouchbaseChangeEntryRepository.class);
  
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
        QueryOptions.queryOptions().parameters(JsonObject.create().put("type", DOCUMENT_TYPE)).scanConsistency(QueryScanConsistency.REQUEST_PLUS));
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
    try{
      collection.upsert(key, toEntity(changeEntry), 
          UpsertOptions.upsertOptions().durability(PersistTo.ACTIVE, ReplicateTo.NONE));  
    } catch (CouchbaseException couchbaseException){
      logger.warn("Error saving change entry with key {}", key, couchbaseException);
      throw new MongockException(couchbaseException);
    }
  }

  @Override
  public void ensureField(Field field) {
    //  nothing to do for couchbase
  }


}
