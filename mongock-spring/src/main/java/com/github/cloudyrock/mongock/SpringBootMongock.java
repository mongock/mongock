package com.github.cloudyrock.mongock;

import io.changock.runner.spring.v5.ChangockSpringApplicationRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;


public class SpringBootMongock implements MongockBase, ApplicationRunner {

  private final ChangockSpringApplicationRunner runner;

  SpringBootMongock(ChangockSpringApplicationRunner runner) {
    this.runner = runner;
  }

  /**
   * @see ApplicationRunner#run(ApplicationArguments)
   */
  @Override
  public void run(ApplicationArguments args) {
    execute();
  }

  public void execute() {
    this.runner.execute();
  }
}
