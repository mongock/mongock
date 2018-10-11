package com.github.cloudyrock.mongock;

import com.mongodb.MongoClient;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;

public class SpringBootMongock extends Mongock implements ApplicationRunner {

    ApplicationContext springContext;

    protected SpringBootMongock(ChangeEntryRepository changeEntryRepository,
        MongoClient mongoClient,
        ChangeService changeService,
        LockChecker lockChecker) {
      super(changeEntryRepository, mongoClient, changeService, lockChecker);
    }

    /**
     * @see ApplicationRunner#run(ApplicationArguments)
     * @see Mongock#execute()
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
      execute();
    }

    /**
     * Overrides the internal argument resolution strategy to allow spring to inject the appropriate parameters.
     *
     * @see Mongock#executeChangeSetMethod(Method, Object)
     */
    @Override
    protected void executeChangeSetMethod(Method changeSetMethod, Object changeLogInstance) throws BeansException, IllegalAccessException, InvocationTargetException {
      List<Object> foundBeanParameters = new ArrayList<>(changeSetMethod.getParameterCount());
      for (Class<?> parameter : changeSetMethod.getParameterTypes()) {
        foundBeanParameters.add(springContext.getBean(parameter));
      }
      changeSetMethod.invoke(changeLogInstance, foundBeanParameters.toArray());
    }

    /**
     * Configures the internal {@link ApplicationContext} to be used when resolving {@link org.springframework.context.annotation.Bean} objects on change set method calls.
     *
     * @return This {@link SpringBootMongock} instance for further configuration
     */
    public SpringBootMongock springContext(ApplicationContext springContext) {
      this.springContext = springContext;
      return this;
    }

}
