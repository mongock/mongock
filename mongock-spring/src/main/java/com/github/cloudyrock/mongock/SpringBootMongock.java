package com.github.cloudyrock.mongock;

import com.mongodb.MongoClient;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SpringBootMongock extends SpringMongock implements ApplicationRunner {

  private ApplicationContext springContext;

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
    List<Object> foundBeanParameters = new ArrayList<>(changeSetMethod.getParameterTypes().length);
    for (Class<?> parameter : changeSetMethod.getParameterTypes()) {
      foundBeanParameters.add(MongoTemplate.class.isAssignableFrom(parameter) ? mongoTemplate : springContext.getBean(parameter));
    }
    changeSetMethod.invoke(changeLogInstance, foundBeanParameters.toArray());
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

}
