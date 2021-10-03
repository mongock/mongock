package io.mongock.runner.springboot.base;

import io.mongock.runner.core.executor.MongockRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class MongockApplicationRunner implements ApplicationRunner {

  private final MongockRunner runner;

  public MongockApplicationRunner(MongockRunner runner) {
    this.runner = runner;
  }


  @Override
  public void run(ApplicationArguments args) throws Exception {
    runner.execute();
  }
}
