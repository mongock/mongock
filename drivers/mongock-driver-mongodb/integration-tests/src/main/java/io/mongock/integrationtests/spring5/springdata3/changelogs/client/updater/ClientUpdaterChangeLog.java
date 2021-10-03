package io.mongock.integrationtests.spring5.springdata3.changelogs.client.updater;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import io.mongock.integrationtests.spring5.springdata3.Mongock4Spring5SpringData3App;
import io.mongock.integrationtests.spring5.springdata3.client.ClientExtended;

import java.util.List;

@ChangeLog(order = "2")
public class ClientUpdaterChangeLog {

    public final static int INITIAL_CLIENTS = 10;


    @ChangeSet(id = "data-updater-with-mongockTemplate", order = "001", author = "mongock")
    public void dataUpdater(MongockTemplate template) {
        List<ClientExtended> clients = template.findAll(ClientExtended.class, Mongock4Spring5SpringData3App.CLIENTS_COLLECTION_NAME);
        clients.stream()
                .map(client -> client.setName(client.getName() + "_updated"))
                .forEach(client -> template.save(client, Mongock4Spring5SpringData3App.CLIENTS_COLLECTION_NAME));

    }


}
