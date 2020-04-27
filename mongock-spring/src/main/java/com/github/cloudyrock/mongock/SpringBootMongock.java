package com.github.cloudyrock.mongock;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;

import java.io.Closeable;
import java.util.Optional;

public class SpringBootMongock extends Mongock implements ApplicationRunner {

  private ApplicationContext springContext;

  SpringBootMongock(ChangeEntryRepository changeEntryRepository,ChangeService changeService, LockChecker lockChecker) {
    super(changeEntryRepository, changeService, lockChecker);
  }

  /**
   * @see ApplicationRunner#run(ApplicationArguments)
   * @see Mongock#execute()
   */
  @Override
  public void run(ApplicationArguments args) {
    execute();
  }

  @Override
  protected Optional<Object> getDependency(Class dependencyType) {
    Optional<Object> dependencyFromParent = super.getDependency(dependencyType);
    if(dependencyFromParent.isPresent()) {
      return dependencyFromParent;
    } else if (springContext != null){
      return Optional.of(springContext.getBean(dependencyType));
    } else {
      return Optional.empty();
    }
  }

  SpringBootMongock springContext(ApplicationContext springContext) {
    this.springContext = springContext;
    return this;
  }
}
