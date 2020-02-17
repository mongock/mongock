package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.github.cloudyrock.mongock.decorator.impl.IndexOperationsDecoratorImpl;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexOperationsProvider;

public interface IndexOperationsProviderDecorator extends IndexOperationsProvider {

    IndexOperationsProvider getimpl();

    MethodInvoker getInvoker();

    @Override
    default IndexOperations indexOps(String collectionName) {
        return new IndexOperationsDecoratorImpl(getInvoker().invoke(()-> getimpl().indexOps(collectionName)), getInvoker());
    }
}
