package io.mongock.runner.core.executor;

public class ExecutorBuilderFixture extends ExecutorBuilderDefault {
  
  private final boolean withSystemUpdate;
  
  public ExecutorBuilderFixture(boolean withSystemUpdate) {
    super();
    this.withSystemUpdate = withSystemUpdate;
  }
  
  @Override
  protected Executor getSystemExecutor() {
    return withSystemUpdate ? super.getSystemExecutor() :
        new Executor () {
          @Override
          public Object executeMigration() {
            return null;
          }
          
          @Override
          public boolean isExecutionInProgress() {
            return false;
          }
    };
  }
}
