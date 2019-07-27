package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.decorator.util.MongockDecoratorBase;
import com.mongodb.bulk.BulkWriteResult;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;

import java.util.List;

public interface  BulkOperationsDecorator extends BulkOperations, MongockDecoratorBase<BulkOperations> {


  @Override
  default BulkOperations insert(Object documents) {
    return null;
  }

  @Override
  default BulkOperations insert(List<?> documents) {
    return null;
  }

  @Override
  default BulkOperations updateOne(Query query, Update update) {
    return null;
  }

  @Override
  default BulkOperations updateOne(List<Pair<Query, Update>> updates) {
    return null;
  }

  @Override
  default BulkOperations updateMulti(Query query, Update update) {
    return null;
  }

  @Override
  default BulkOperations updateMulti(List<Pair<Query, Update>> updates) {
    return null;
  }

  @Override
  default BulkOperations upsert(Query query, Update update) {
    return null;
  }

  @Override
  default BulkOperations upsert(List<Pair<Query, Update>> updates) {
    return null;
  }

  @Override
  default BulkOperations remove(Query remove) {
    return null;
  }

  @Override
  default BulkOperations remove(List<Query> removes) {
    return null;
  }

  @Override
  default BulkWriteResult execute() {
    return null;
  }
}
