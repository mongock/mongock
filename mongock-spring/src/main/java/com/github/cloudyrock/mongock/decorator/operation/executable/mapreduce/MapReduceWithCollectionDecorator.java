package com.github.cloudyrock.mongock.decorator.operation.executable.mapreduce;

import com.github.cloudyrock.mongock.decorator.operation.executable.mapreduce.impl.MapReduceWithProjectionDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public interface MapReduceWithCollectionDecorator<T> extends ExecutableMapReduceOperation.MapReduceWithCollection<T>, MapReduceWithQueryDecorator<T> {

    @Override
    ExecutableMapReduceOperation.MapReduceWithCollection<T> getImpl();


    @Override
    default ExecutableMapReduceOperation.MapReduceWithProjection<T> inCollection(String collection) {
        return getInvoker().invoke(()-> new MapReduceWithProjectionDecoratorImpl<>(getImpl().inCollection(collection), getInvoker()));
    }
}
