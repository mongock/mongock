package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.decorator.util.MongockDecoratorBase;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.IndexOperations;

import java.util.List;

public interface IndexOperationsDecorator extends IndexOperations, MongockDecoratorBase<IndexOperations> {

  
  
  @Override
  default String ensureIndex(IndexDefinition indexDefinition) {
    return null;
  }

  @Override
  default void dropIndex(String name) {

  }

  @Override
  default void dropAllIndexes() {

  }

  @Override
  default List<IndexInfo> getIndexInfo() {
    return null;
  }
}
