package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.client;


import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.client.Client;

import java.util.List;

import static com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.Mongock4Spring5SpringData3App.CLIENTS_COLLECTION_NAME;

@ChangeLog(order = "3")
public class ClientUpdater2ChangeLog {

    public final static int INITIAL_CLIENTS = 10;


    @ChangeSet(id = "data-updater-with-mongockTemplate-2", order = "001", author = "mongock")
    public void dataUpdater(MongockTemplate template) {

        List<Client> clients = template.findAll(Client.class, CLIENTS_COLLECTION_NAME);

        clients.stream()
                .map(client -> client.setName(client.getName() + "_updated_2"))
                .forEach(client -> template.save(client, CLIENTS_COLLECTION_NAME));

    }


}
