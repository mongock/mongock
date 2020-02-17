package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.github.cloudyrock.mongock.decorator.impl.MapReduceWithReduceFunctionDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public interface MapReduceWithMapFunctionDecorator<T> extends ExecutableMapReduceOperation.MapReduceWithMapFunction<T> {

    ExecutableMapReduceOperation.MapReduceWithMapFunction<T> getImpl();

    MethodInvoker getInvoker();

    @Override
    default ExecutableMapReduceOperation.MapReduceWithReduceFunction<T> map(String mapFunction) {
        return getInvoker().invoke(()-> new MapReduceWithReduceFunctionDecoratorImpl<>(getImpl().map(mapFunction), getInvoker()));
    }
}
