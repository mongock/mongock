package io.mongock.driver.mongodb.springdata.v2.decorator;

import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.IndexOperations;

import java.util.List;

public interface IndexOperationsDecorator extends IndexOperations {

    IndexOperations getImpl();

    LockGuardInvoker getInvoker();

    @Override
    default String ensureIndex(IndexDefinition indexDefinition) {
        return getInvoker().invoke(()-> getImpl().ensureIndex(indexDefinition));
    }

    @Override
    default void dropIndex(String name) {
        getInvoker().invoke(()-> getImpl().dropIndex(name));
    }

    @Override
    default void dropAllIndexes() {
        getInvoker().invoke(()-> getImpl().dropAllIndexes());
    }

    @Override
    default List<IndexInfo> getIndexInfo() {
        return getInvoker().invoke(()-> getImpl().getIndexInfo());
    }
}
