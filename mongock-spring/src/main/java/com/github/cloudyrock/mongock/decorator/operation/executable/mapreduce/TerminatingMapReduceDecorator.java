package com.github.cloudyrock.mongock.decorator.operation.executable.mapreduce;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

import java.util.List;

public interface TerminatingMapReduceDecorator<T> extends ExecutableMapReduceOperation.TerminatingMapReduce<T> {
    ExecutableMapReduceOperation.TerminatingMapReduce<T> getImpl();

    MethodInvoker getInvoker();

    @Override
    default List<T> all() {
        return getInvoker().invoke(() -> getImpl().all());
    }
}
