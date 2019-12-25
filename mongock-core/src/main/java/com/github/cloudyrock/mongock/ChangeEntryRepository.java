package com.github.cloudyrock.mongock;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static com.github.cloudyrock.mongock.ChangeEntry.EXECUTION_ID;
import static com.github.cloudyrock.mongock.ChangeEntry.KEY_AUTHOR;
import static com.github.cloudyrock.mongock.ChangeEntry.KEY_CHANGEID;

/**
 *
 * @since 27/07/2014
 */
class ChangeEntryRepository extends MongoRepository {

  ChangeEntryRepository(String collectionName, MongoDatabase mongoDatabase) {
    super(mongoDatabase, collectionName, new String[]{EXECUTION_ID, KEY_AUTHOR, KEY_CHANGEID});
  }

  boolean isNewChange(ChangeEntry changeEntry) throws MongockException {
    Document entry = collection.find(buildSearchQueryDBObject(changeEntry)).first();
    return entry == null || entry.isEmpty();
  }

  void save(ChangeEntry changeEntry) throws MongockException {
    collection.insertOne(changeEntry.buildFullDBObject());
  }

  private Document buildSearchQueryDBObject(ChangeEntry entry) {
    return new Document()
        .append(EXECUTION_ID, entry.getExecutionId())
        .append(KEY_CHANGEID, entry.getChangeId())
        .append(KEY_AUTHOR, entry.getAuthor());
  }

}
