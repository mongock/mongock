package com.github.cloudyrock.mongock;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SpringBootMongock extends Mongock implements ApplicationRunner {

  private ApplicationContext springContext;
  private MongoTemplate mongoTemplate;

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
    List<Object> changelogInvocationParameters = new ArrayList<>(changeSetMethod.getParameterTypes().length);
    for (Class<?> parameter : changeSetMethod.getParameterTypes()) {
      if (MongoTemplate.class.isAssignableFrom(parameter)) {
        changelogInvocationParameters.add(mongoTemplate);

      } else if (DB.class.isAssignableFrom(parameter)) {
        throw new UnsupportedOperationException("DB not supported by Mongock. Please use MongoDatabase");

      } else if (MongoDatabase.class.isAssignableFrom(parameter)) {
        changelogInvocationParameters.add(this.changelogMongoDatabase);

      } else {
        changelogInvocationParameters.add(springContext.getBean(parameter));
      }
    }
    changeSetMethod.invoke(changeLogInstance, changelogInvocationParameters.toArray());
  }

  /**
   * Configures the internal {@link ApplicationContext} to be used when resolving {@link org.springframework.context.annotation.Bean} objects on change set method calls.
   *
   * @return This {@link SpringBootMongock} instance for further configuration
   */
  SpringBootMongock springContext(ApplicationContext springContext) {
    this.springContext = springContext;
    return this;
  }

  /**
   * Sets pre-configured {@link MongoTemplate} instance to use by the Mongock
   *
   * @param mongoTemplate instance of the {@link MongoTemplate}
   * @return SpringMongock object for fluent interface
   */
  SpringBootMongock setMongoTemplate(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
    return this;
  }

}
