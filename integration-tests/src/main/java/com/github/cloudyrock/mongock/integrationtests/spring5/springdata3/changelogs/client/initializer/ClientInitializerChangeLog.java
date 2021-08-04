package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.client.initializer;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.api.lock.guard.proxy.LockGuardProxyFactory;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.client.ClientExtended;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.client.ClientRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ChangeLog(order = "1")
public class ClientInitializerChangeLog {

    public final static int INITIAL_CLIENTS = 10;

    @ChangeSet(id = "data-initializer-with-repository", order = "001", author = "mongock")
    public void dataInitializer(ClientRepository clientRepository) {

        LockGuardProxyFactory.isProxy(clientRepository);
        List<ClientExtended> clients = IntStream.range(0, INITIAL_CLIENTS)
                .mapToObj(i -> new ClientExtended("name-" + i, "email-" + i, "phone" + i, "country" + i))
                .collect(Collectors.toList());
        List<ClientExtended> result = clientRepository.saveAll(clients);
    }
}
