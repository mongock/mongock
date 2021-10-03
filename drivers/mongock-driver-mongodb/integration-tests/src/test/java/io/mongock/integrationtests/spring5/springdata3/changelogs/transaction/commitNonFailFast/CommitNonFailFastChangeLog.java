package io.mongock.integrationtests.spring5.springdata3.changelogs.transaction.commitNonFailFast;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import io.mongock.integrationtests.spring5.springdata3.client.ClientExtended;
import io.mongock.integrationtests.spring5.springdata3.client.ClientRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ChangeLog(order = "2")
public class CommitNonFailFastChangeLog {

    @ChangeSet(id = "method-successful", order = "001", author = "mongock")
    public void methodSuccessful(ClientRepository clientRepository) {
        List<ClientExtended> clients = IntStream.range(0, 10)
                .mapToObj(i -> new ClientExtended("name-" + i, "email-" + i, "phone" + i, "country" + i))
                .collect(Collectors.toList());
        List<ClientExtended> result = clientRepository.saveAll(clients);
    }

    @ChangeSet(id = "method-failing", order = "002", author = "mongock", failFast = false)
    public void methodFailing(ClientRepository clientRepository) {
        if(true) {
            throw new RuntimeException("Transaction error");
        }
    }


}
