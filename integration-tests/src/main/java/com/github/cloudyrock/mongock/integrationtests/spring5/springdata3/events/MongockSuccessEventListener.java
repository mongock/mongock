package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.events;

import com.github.cloudyrock.springboot.base.events.SpringMigrationSuccessEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class MongockSuccessEventListener implements ApplicationListener<SpringMigrationSuccessEvent> {

    @Override
    public void onApplicationEvent(SpringMigrationSuccessEvent event) {
        System.out.println("[EVENT LISTENER] - Mongock finished successfully");
    }

}
