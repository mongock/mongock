package io.mongock.driver.mongodb.springdata.v3.decorator;

import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.mongock.driver.mongodb.springdata.v3.decorator.impl.BulkOperationsDecoratorImpl;
import com.mongodb.bulk.BulkWriteResult;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;

import java.util.List;

public interface BulkOperationsDecorator extends BulkOperations {

    BulkOperations getImpl();

    LockGuardInvoker getInvoker();

    @Override
    default BulkOperations insert(Object documents) {
        return new BulkOperationsDecoratorImpl(getInvoker().invoke(() -> getImpl().insert(documents)), getInvoker());
    }

    @Override
    default BulkOperations insert(List<?> documents) {
        return new BulkOperationsDecoratorImpl(getInvoker().invoke(() -> getImpl().insert(documents)), getInvoker());
    }

    @Override
    default BulkOperations updateOne(Query query, Update update) {
        return new BulkOperationsDecoratorImpl(getInvoker().invoke(() -> getImpl().updateOne(query, update)), getInvoker());
    }

    @Override
    default BulkOperations updateOne(List<Pair<Query, Update>> updates) {
        return new BulkOperationsDecoratorImpl(getInvoker().invoke(() -> getImpl().updateOne(updates)), getInvoker());
    }

    @Override
    default BulkOperations updateMulti(Query query, Update update) {
        return new BulkOperationsDecoratorImpl(getInvoker().invoke(() -> getImpl().updateMulti(query, update)), getInvoker());
    }

    @Override
    default BulkOperations updateMulti(List<Pair<Query, Update>> updates) {
        return new BulkOperationsDecoratorImpl(getInvoker().invoke(() -> getImpl().updateMulti(updates)), getInvoker());
    }

    @Override
    default BulkOperations upsert(Query query, Update update) {
        return new BulkOperationsDecoratorImpl(getInvoker().invoke(() -> getImpl().upsert(query, update)), getInvoker());
    }

    @Override
    default BulkOperations upsert(List<Pair<Query, Update>> updates) {
        return new BulkOperationsDecoratorImpl(getInvoker().invoke(() -> getImpl().upsert(updates)), getInvoker());
    }

    @Override
    default BulkOperations remove(Query remove) {
        return new BulkOperationsDecoratorImpl(getInvoker().invoke(() -> getImpl().remove(remove)), getInvoker());
    }

    @Override
    default BulkOperations remove(List<Query> removes) {
        return new BulkOperationsDecoratorImpl(getInvoker().invoke(() -> getImpl().remove(removes)), getInvoker());
    }

    @Override
    default BulkOperations replaceOne(Query query, Object replacement, FindAndReplaceOptions options) {
        return new BulkOperationsDecoratorImpl(getInvoker().invoke(() -> getImpl().replaceOne(query, replacement, options)), getInvoker());
    }

    @Override

    default BulkOperations replaceOne(Query query, Object replacement) {
      return new BulkOperationsDecoratorImpl(getInvoker().invoke(() -> getImpl().replaceOne(query, replacement)), getInvoker());
    }

    @Override
    default BulkWriteResult execute() {
        return getInvoker().invoke(() -> getImpl().execute());
    }
}
