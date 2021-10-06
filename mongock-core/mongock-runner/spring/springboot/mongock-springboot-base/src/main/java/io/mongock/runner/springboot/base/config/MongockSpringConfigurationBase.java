package io.mongock.runner.springboot.base.config;

/**
 * It needs to be loaded explicitly in the Driver importer(example MongoSpringDataImporter.java)
 * So each driver can override the MongockSpringConfiguration in case it adds any new parameter
 */
public interface MongockSpringConfigurationBase {


  SpringRunnerType getRunnerType();

  void setRunnerType(SpringRunnerType runnerType);

  boolean isTestEnabled();

  void setTestEnabled(boolean testEnabled);

}
