package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.testConfiguration;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.client.Client;
import org.bson.Document;

import java.util.List;

import static com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.Mongock4Spring5SpringData3App.CLIENTS_COLLECTION_NAME;

@ChangeLog(order = "2")
public class TestConfigurationChangeLog {

    public static final String COLLECTION_NAME = "testCollection";

    @ChangeSet(id = "testConfiguration-with-mongockTemplate", order = "001", author = "mongock", failFast = false)
    public void testConfigurationWithMongockTemplate(MongockTemplate template) {

        template.getCollection(COLLECTION_NAME).insertOne(new Document().append("field", "value"));
    }


}
