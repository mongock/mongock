package io.mongock.runner.springboot.base.util;


import io.mongock.driver.api.driver.ConnectionDriver;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
//@Import(MongockContextStub.class)
public class Application {


    public static void main(String[] args) {
        getSpringAppBuilder().run(args);
    }


    public static SpringApplicationBuilder getSpringAppBuilder() {
        return new SpringApplicationBuilder().sources(Application.class);
    }


}
