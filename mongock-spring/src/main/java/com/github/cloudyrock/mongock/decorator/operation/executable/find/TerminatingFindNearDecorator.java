package com.github.cloudyrock.mongock.decorator.operation.executable.find;

import com.github.cloudyrock.mongock.decorator.util.Invokable;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public interface TerminatingFindNearDecorator<T> extends Invokable, ExecutableFindOperation.TerminatingFindNear<T> {

  ExecutableFindOperation.TerminatingFindNear<T> getImpl();

  @Override
  default GeoResults<T> all() {
    return getInvoker().invoke(()-> getImpl().all());
  }
}
