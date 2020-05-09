package com.github.cloudyrock.mongock;

import io.changock.runner.standalone.StandaloneChangockRunner;

public class MongockStandaloneBuilder extends MongockBuilderBase<MongockStandaloneBuilder, MongockStandalone> {

  /**
   * <p>Builder constructor takes the new API changelog scan package as parameter.
   *
   * @param changeLogsScanPackage package path where the changelogs are located
   */
  public MongockStandaloneBuilder(String changeLogsScanPackage) {
    super(changeLogsScanPackage);
  }

  public MongockStandalone build() {
    return new MongockStandalone(getBuilder(driver).build());
  }

  private StandaloneChangockRunner.Builder getBuilder(MongockConnectionDriver driver) {
    return StandaloneChangockRunner.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(changeLogsScanPackage)
        .setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock)
        .setLockConfig(lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries)
        .setEnabled(enabled)
        .setStartSystemVersion(startSystemVersion)
        .setEndSystemVersion(endSystemVersion)
        .withMetadata(metadata)
        .overrideAnnoatationProcessor(new MongockAnnotationProcessor());
  }

  @Override
  protected MongockStandaloneBuilder getInstance() {
    return this;
  }


}
