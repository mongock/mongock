package io.mongock.integrationtests.spring5.springdata3.client;

import io.mongock.integrationtests.spring5.springdata3.Mongock4Spring5SpringData3App;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository(Mongock4Spring5SpringData3App.CLIENTS_COLLECTION_NAME)
public interface ClientRepository extends MongoRepository<ClientExtended, String> {

}
