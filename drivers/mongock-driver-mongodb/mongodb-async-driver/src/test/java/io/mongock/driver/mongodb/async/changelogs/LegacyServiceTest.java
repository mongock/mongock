package io.mongock.driver.mongodb.async.changelogs;


import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.mongock.api.config.LegacyMigration;
import io.mongock.api.config.LegacyMigrationMappingFields;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.driver.api.entry.ChangeEntryExecuted;
import io.mongock.driver.api.entry.ChangeEntryService;
import io.mongock.driver.mongodb.async.util.FindPublisherMock;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LegacyServiceTest {


  @Test
  public void legacyServiceWithCorruptedState() {
    long now = System.currentTimeMillis();
    ChangeEntryService changeEntryService = mockChangeEntryService();

    new LegacyService().executeMigration(getLegacyMigration(), mockDatabase(), changeEntryService);

    ArgumentCaptor<ChangeEntry> entryCaptor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(2)).saveOrUpdate(entryCaptor.capture());

    List<ChangeEntry> savedChangeEntries = entryCaptor.getAllValues();
    ChangeEntry c0 = savedChangeEntries.get(0);
    assertEquals("changeId-value-0", c0.getChangeId());
    ChangeEntry c1 = savedChangeEntries.get(1);
    assertEquals("changeId-value-1", c1.getChangeId());
    assertTrue(c1.getTimestamp().getTime() > now);
  }

  private LegacyMigration getLegacyMigration() {
    LegacyMigration lm = new LegacyMigration("origin-collection");
    lm.setMappingFields(new LegacyMigrationMappingFields(
        "changeId-field", "author-field", "timestamp-field", "changeLogClass-field", "changeSetMethod-field", "metadata-field"
    ));
    return lm;
  }

  private ChangeEntryService mockChangeEntryService() {
    ChangeEntryService service = mock(ChangeEntryService.class);

    ChangeEntry c0 = getChangeEntry(0);//from legacy and not in target collection
    ChangeEntry c1 = getChangeEntry(1);//from legacy and NOT executed(NOT OK)
    ChangeEntry c2 = getChangeEntry(2);//from legacy and executed(OK)
    ChangeEntry c3 = getChangeEntry(3);//not from legacy
    ChangeEntryExecuted c2Ex = new ChangeEntryExecuted(c2);
    ChangeEntryExecuted c3Ex = new ChangeEntryExecuted(c3);

    when(service.getEntriesLog()).thenReturn(Arrays.asList(c1, c2, c3));

    when(service.getExecuted()).thenReturn(Arrays.asList(c2Ex, c3Ex));

    return service;
  }


  @SuppressWarnings("unchecked")
  private MongoDatabase mockDatabase() {

    FindPublisherMock<Document> spy = Mockito.spy(new FindPublisherMock<>(Arrays.asList(getDocument(0), getDocument(1), getDocument(2))));
    MongoCollection<Document> collection = (MongoCollection<Document>) mock(MongoCollection.class);
    when(collection.find()).thenReturn(spy);
    when(collection.find(any(Bson.class))).thenReturn(spy);
    MongoDatabase mongoDatabase = mock(MongoDatabase.class);
    when(mongoDatabase.getCollection(Mockito.anyString())).thenReturn(collection);
    return mongoDatabase;


  }

  private Document getDocument(int i) {
    return new Document()
        .append("changeId-field", "changeId-value-" + i)
        .append("author-field", "author-value-" + i)
        .append("timestamp-field", new Date(1639507677000L))//Tue Dec 14 18:47:57 WET 2021
        .append("changeLogClass-field", "changeLogClass-value-" + i)
        .append("changeSetMethod-field", "changeSetMethod-value-" + i)
        .append("metadata-field", null);
  }

  private ChangeEntry getChangeEntry(int i) {
    return new ChangeEntry(null, "changeId-value-" + i, "author-value-" + i, new Date(), null, null, null, null, -1, null, null, null);
  }


}
