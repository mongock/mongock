package com.github.cloudyrock.mongock.samples.v4.spring5.springdata3.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import static com.github.cloudyrock.mongock.samples.v4.spring5.springdata3.Mongock4Spring5SpringData3App.CLIENTS_COLLECTION_NAME;
import com.github.cloudyrock.mongock.samples.v4.spring5.springdata3.client.Client;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@ChangeLog
public class ClientChangeLog {

  private final static int INITIAL_CLIENTS = 10;

  @ChangeSet(id = "data-initializer", order = "001", author = "mongock")
  public void dataInitializer(MongockTemplate template) {

    List<Client> clients = IntStream.range(0, INITIAL_CLIENTS)
        .mapToObj(i -> new Client("name-" + i, "email-" + i, "phone" + i, "country" + i))
        .collect(Collectors.toList());
    template.insert(clients, CLIENTS_COLLECTION_NAME);
  }

  @ChangeSet(id = "data-updater", order = "002", author = "mongock")
  public void dataUpdater(MongockTemplate template) {

    List<Client> clients = template.findAll(Client.class, CLIENTS_COLLECTION_NAME);

    clients.stream()
        .map(client -> client.setName(client.getName() + "_updated"))
    .forEach(client -> template.save(client, CLIENTS_COLLECTION_NAME));

  }


}
