package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.client.updater;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.client.Client;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.client.ClientRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.Mongock4Spring5SpringData3App.CLIENTS_COLLECTION_NAME;

@ChangeLog(order = "2")
public class ClientUpdaterChangeLog {

    public final static int INITIAL_CLIENTS = 10;


    @ChangeSet(id = "data-updater-with-mongockTemplate", order = "001", author = "mongock")
    public void dataUpdater(MongockTemplate template) {
        List<Client> clients = template.findAll(Client.class, CLIENTS_COLLECTION_NAME);
        clients.stream()
                .map(client -> client.setName(client.getName() + "_updated"))
                .forEach(client -> template.save(client, CLIENTS_COLLECTION_NAME));

    }


}
