package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator;

import com.mongodb.ClientSessionOptions;
import com.mongodb.ServerAddress;
import com.mongodb.TransactionOptions;
import com.mongodb.client.ClientSession;
import com.mongodb.client.TransactionBody;
import com.mongodb.session.ServerSession;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.bson.BsonDocument;
import org.bson.BsonTimestamp;

public interface ClientSessionDecorator extends ClientSession {

    ClientSession getImpl();
    LockGuardInvoker getInvoker();

    @Override
    default ServerAddress getPinnedServerAddress() {
        return getImpl().getPinnedServerAddress();
    }

    @Override
    default void setPinnedServerAddress(ServerAddress address) {
        getImpl().setPinnedServerAddress(address);
    }

    @Override
    default BsonDocument getRecoveryToken() {
        return getImpl().getRecoveryToken();
    }

    @Override
    default void setRecoveryToken(BsonDocument recoveryToken) {
        getImpl().setRecoveryToken(recoveryToken);
    }

    @Override
    default ClientSessionOptions getOptions() {
        return getImpl().getOptions();
    }

    @Override
    default boolean isCausallyConsistent() {
        return getImpl().isCausallyConsistent();
    }

    @Override
    default Object getOriginator() {
        return getImpl().getOriginator();
    }

    @Override
    default ServerSession getServerSession() {
        return getImpl().getServerSession();
    }

    @Override
    default BsonTimestamp getOperationTime() {
        return getImpl().getOperationTime();
    }

    @Override
    default void advanceOperationTime(BsonTimestamp operationTime) {
        getImpl().advanceOperationTime(operationTime);
    }

    @Override
    default void advanceClusterTime(BsonDocument clusterTime) {
        getImpl().advanceClusterTime(clusterTime);
    }

    @Override
    default BsonDocument getClusterTime() {
        return getImpl().getClusterTime();
    }

    @Override
    default void close() {
        getInvoker().invoke(()-> getImpl().close());
    }

    @Override
    default boolean hasActiveTransaction() {
        return getImpl().hasActiveTransaction();
    }

    @Override
    default boolean notifyMessageSent() {
        return getImpl().notifyMessageSent();
    }

    @Override
    default TransactionOptions getTransactionOptions() {
        return getImpl().getTransactionOptions();
    }

    @Override
    default void startTransaction() {
        getInvoker().invoke(()->getImpl().startTransaction());
    }

    @Override
    default void startTransaction(TransactionOptions transactionOptions) {
        getInvoker().invoke(()->getImpl().startTransaction(transactionOptions));
    }

    @Override
    default void commitTransaction() {
        getInvoker().invoke(()->getImpl().commitTransaction());
    }

    @Override
    default void abortTransaction() {
        getInvoker().invoke(()->getImpl().abortTransaction());
    }

    @Override
    default <T> T withTransaction(TransactionBody<T> transactionBody) {
        return getInvoker().invoke(()->getImpl().withTransaction(transactionBody));
    }

    @Override
    default <T> T withTransaction(TransactionBody<T> transactionBody, TransactionOptions options) {
        return getInvoker().invoke(()->getImpl().withTransaction(transactionBody, options));
    }
}
