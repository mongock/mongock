package io.mongock.integrationtests.spring5.springdata3.events;

import io.mongock.runner.spring.base.events.SpringMigrationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class MongockStartedEventListener implements ApplicationListener<SpringMigrationStartedEvent> {

    @Override
    public void onApplicationEvent(SpringMigrationStartedEvent event) {
        System.out.println("[EVENT LISTENER] - Mongock STARTED successfully");
    }

}
