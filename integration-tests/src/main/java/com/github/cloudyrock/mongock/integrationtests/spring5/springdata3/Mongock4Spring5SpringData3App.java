package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3;


import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.client.ClientRepository;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.spring.DateToZonedDateTimeConverter;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.spring.ZonedDateTimeToDateConverter;
import com.github.cloudyrock.springboot.v2_2.EnableMongock;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;
import java.util.List;

@EnableMongock
@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = ClientRepository.class)
public class Mongock4Spring5SpringData3App {

    public final static String CLIENTS_COLLECTION_NAME = "clientCollection";

    public static void main(String[] args) {
        getSpringAppBuilder().run(args);
    }


    public static SpringApplicationBuilder getSpringAppBuilder() {
        return new SpringApplicationBuilder().sources(Mongock4Spring5SpringData3App.class);
    }

    // It requires MongoDb with a replicaSet
    @Bean
    @ConditionalOnExpression("${mongock.transactionable:false}")
    MongoTransactionManager transactionManager(MongoTemplate mongoTemplate) {
        //creating the collection in advance is required, because cannot be created during a transaction
        mongoTemplate.createCollection("clientCollection");
        return new MongoTransactionManager(mongoTemplate.getMongoDbFactory());
    }

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(DateToZonedDateTimeConverter.INSTANCE);
        converters.add(ZonedDateTimeToDateConverter.INSTANCE);
        return new MongoCustomConversions(converters);
    }
}
