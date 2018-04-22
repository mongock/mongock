package com.github.cloudyrock.mongock;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static com.github.cloudyrock.mongock.ChangeEntry.KEY_AUTHOR;
import static com.github.cloudyrock.mongock.ChangeEntry.KEY_CHANGEID;

/**
 * @author lstolowski
 * @since 27/07/2014
 */
class ChangeEntryRepository extends MongoRepository {

  ChangeEntryRepository(String collectionName, MongoDatabase mongoDatabase) {
    super(mongoDatabase, collectionName, new String[]{KEY_AUTHOR, KEY_CHANGEID});
  }

  boolean isNewChange(ChangeEntry changeEntry) throws MongockException {
    verifyDbConnectionAndEnsureIndex();
    Document entry = collection.find(buildSearchQueryDBObject(changeEntry)).first();

    return entry == null;
  }

  void save(ChangeEntry changeEntry) throws MongockException {
    verifyDbConnectionAndEnsureIndex();
    collection.insertOne(changeEntry.buildFullDBObject());
  }

  private Document buildSearchQueryDBObject(ChangeEntry entry) {
    return new Document()
        .append(KEY_CHANGEID, entry.getChangeId())
        .append(KEY_AUTHOR, entry.getAuthor());
  }

}
