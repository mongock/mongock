package io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.mapreduce;

import io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.mapreduce.impl.TerminatingMapReduceDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;

public interface MapReduceWithQueryDecorator<T> extends ExecutableMapReduceOperation.MapReduceWithQuery<T>, TerminatingMapReduceDecorator<T> {


    @Override
    ExecutableMapReduceOperation.MapReduceWithQuery<T> getImpl();

    @Override
    default ExecutableMapReduceOperation.TerminatingMapReduce<T> matching(Query query) {
        return getInvoker().invoke(()-> new TerminatingMapReduceDecoratorImpl<>(getImpl().matching(query), getInvoker()));
    }

  @Override
  default ExecutableMapReduceOperation.TerminatingMapReduce<T> matching(CriteriaDefinition criteria) {
    return getInvoker().invoke(()-> new TerminatingMapReduceDecoratorImpl<>(getImpl().matching(criteria), getInvoker()));
  }

}
