package io.mongock.api.config.executor;

import io.mongock.api.config.TransactionStrategy;
import java.util.List;

import java.util.Map;
import java.util.Optional;

public interface ChangeExecutorConfiguration {
  Map<String, Object> getMetadata();

  String getServiceIdentifier();

  boolean isTrackIgnored();

  Optional<Boolean> getTransactionEnabled();
  
  TransactionStrategy getTransactionStrategy();

  @Deprecated
  String getDefaultMigrationAuthor();

  String getDefaultAuthor();
  
  List<String> getMigrationScanPackage();
  
  String getStartSystemVersion();
  
  String getEndSystemVersion();

}
