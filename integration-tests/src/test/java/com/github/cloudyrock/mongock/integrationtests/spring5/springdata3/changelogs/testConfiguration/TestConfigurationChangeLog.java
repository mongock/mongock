package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.testConfiguration;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import org.bson.Document;

@ChangeLog(order = "2")
public class TestConfigurationChangeLog {

    public static final String COLLECTION_NAME = "testCollection";

    @ChangeSet(id = "testConfiguration-with-mongockTemplate", order = "001", author = "mongock", failFast = false)
    public void testConfigurationWithMongockTemplate(MongockTemplate template) {

        template.getCollection(COLLECTION_NAME).insertOne(new Document().append("field", "value"));
    }


}
