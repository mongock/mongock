package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.transaction.successful;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.client.Client;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.Mongock4Spring5SpringData3App.CLIENTS_COLLECTION_NAME;

@ChangeLog(order = "2")
public class TransactionSuccessfulChangeLog {

    @ChangeSet(id = "method-successful", order = "001", author = "mongock")
    public void methodSuccessful(MongockTemplate template) {
        List<Client> clients = IntStream.range(0, 10)
                .mapToObj(i -> new Client("name-" + i, "email-" + i, "phone" + i, "country" + i))
                .collect(Collectors.toList());
        Collection<Client> result = template.insert(clients, CLIENTS_COLLECTION_NAME);
    }

    @ChangeSet(id = "method-failing", order = "002", author = "mongock")
    public void methodFailing(MongockTemplate template) {
    }


}
