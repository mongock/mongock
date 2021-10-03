package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.mapreduce;

import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public interface ExecutableMapReduceDecorator<T> extends ExecutableMapReduceOperation.ExecutableMapReduce<T>,
        MapReduceWithMapFunctionDecorator<T>, MapReduceWithReduceFunctionDecorator<T>,
        MapReduceWithCollectionDecorator<T>, MapReduceWithProjectionDecorator<T>, MapReduceWithOptionsDecorator<T> {
    @Override
    ExecutableMapReduceOperation.ExecutableMapReduce<T> getImpl();

}
