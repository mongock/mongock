package com.github.cloudyrock.mongock;

import io.changock.runner.standalone.StandaloneChangockRunner;

/**
 * Mongock runner
 *
 * @since 26/07/2014
 */
public class MongockStandalone implements MongockBase {

  private final StandaloneChangockRunner runner;

  MongockStandalone(StandaloneChangockRunner runner) {
    this.runner = runner;
  }

  public void execute() {
    this.runner.execute();
  }

}
