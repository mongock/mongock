package io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.mapreduce;

import io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.mapreduce.impl.MapReduceWithQueryDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public interface MapReduceWithProjectionDecorator<T> extends ExecutableMapReduceOperation.MapReduceWithProjection<T>, MapReduceWithQueryDecorator<T> {

    @Override
    ExecutableMapReduceOperation.MapReduceWithProjection<T> getImpl();

    @Override
    default  <R> ExecutableMapReduceOperation.MapReduceWithQuery<R> as(Class<R> resultType) {
        return getInvoker().invoke(()-> new MapReduceWithQueryDecoratorImpl<>(getImpl().as(resultType), getInvoker()));
    }
}
