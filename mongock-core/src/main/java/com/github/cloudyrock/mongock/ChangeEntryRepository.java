package com.github.cloudyrock.mongock;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static com.github.cloudyrock.mongock.ChangeEntry.KEY_EXECUTION_ID;
import static com.github.cloudyrock.mongock.ChangeEntry.KEY_AUTHOR;
import static com.github.cloudyrock.mongock.ChangeEntry.KEY_CHANGE_ID;

/**
 *
 * @since 27/07/2014
 */
class ChangeEntryRepository extends MongoRepository {

  ChangeEntryRepository(String collectionName, MongoDatabase mongoDatabase) {
    super(mongoDatabase, collectionName, new String[]{KEY_EXECUTION_ID, KEY_AUTHOR, KEY_CHANGE_ID});
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
        .append(KEY_CHANGE_ID, entry.getChangeId())
        .append(KEY_AUTHOR, entry.getAuthor());
  }

}
