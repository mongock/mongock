package io.mongock.driver.couchbase.entry;

import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonCreator;
import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonInclude;
import com.couchbase.client.java.json.JsonObject;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.driver.api.entry.ChangeState;
import io.mongock.driver.api.entry.ChangeType;

import java.util.Date;

/**
 * ChangeEntry implementation for Couchbase, basically adds a way to deserialize the object from JSON.
 * 
 * @author Tigran Babloyan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CouchbaseChangeEntry extends ChangeEntry {
  
  @JsonCreator
  public CouchbaseChangeEntry(JsonObject jsonObject){
    super(jsonObject.getString(ChangeEntry.KEY_EXECUTION_ID),
        jsonObject.getString(ChangeEntry.KEY_CHANGE_ID),
        jsonObject.getString(ChangeEntry.KEY_AUTHOR),
        jsonObject.get(ChangeEntry.KEY_TIMESTAMP) != null ? new Date (jsonObject.getLong(ChangeEntry.KEY_TIMESTAMP)) : null,
        jsonObject.get(ChangeEntry.KEY_STATE) != null ? ChangeState.valueOf(jsonObject.getString(ChangeEntry.KEY_STATE)) : null,
        jsonObject.get(ChangeEntry.KEY_TYPE) != null ? ChangeType.valueOf(jsonObject.getString(ChangeEntry.KEY_TYPE)) : null,
        jsonObject.getString(ChangeEntry.KEY_CHANGELOG_CLASS),
        jsonObject.getString(ChangeEntry.KEY_CHANGESET_METHOD),
        jsonObject.getLong(ChangeEntry.KEY_EXECUTION_MILLIS),
        jsonObject.getString(ChangeEntry.KEY_EXECUTION_HOST_NAME),
        jsonObject.get(ChangeEntry.KEY_METADATA) != null ? jsonObject.getObject(ChangeEntry.KEY_METADATA).toMap() : null,
        jsonObject.getString(ChangeEntry.KEY_ERROR_TRACE),
        jsonObject.getBoolean(ChangeEntry.KEY_SYSTEM_CHANGE));
  }

}
