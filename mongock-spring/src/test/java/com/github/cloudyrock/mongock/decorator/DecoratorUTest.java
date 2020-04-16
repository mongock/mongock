package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.LockChecker;
import com.github.cloudyrock.mongock.decorator.impl.BulkOperationsDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.impl.ClientSessionDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.impl.CloseableIteratorDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.impl.IndexOperationsDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.impl.MongockTemplate;
import com.github.cloudyrock.mongock.decorator.impl.ScriptOperationsDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.impl.SessionCallbackDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.impl.SessionScopedDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.aggregation.impl.AggregationWithAggregationDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.aggregation.impl.AggregationWithCollectionDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.aggregation.impl.ExecutableAggregationDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.aggregation.impl.TerminatingAggregationDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.find.impl.ExecutableFindDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.find.impl.FindDistinctDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.find.impl.FindWithCollectionDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.find.impl.FindWithProjectionDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.find.impl.FindWithQueryDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.find.impl.TerminatingDistinctDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.find.impl.TerminatingFindDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.find.impl.TerminatingFindNearDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.insert.impl.ExecutableInsertDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.insert.impl.InsertWithBulkModeDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.insert.impl.InsertWithCollectionDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.insert.impl.TerminatingBulkInsertDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.insert.impl.TerminatingInsertDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.mapreduce.impl.ExecutableMapReduceDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.mapreduce.impl.MapReduceWithCollectionDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.mapreduce.impl.MapReduceWithMapFunctionDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.mapreduce.impl.MapReduceWithOptionsDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.mapreduce.impl.MapReduceWithProjectionDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.mapreduce.impl.MapReduceWithQueryDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.mapreduce.impl.MapReduceWithReduceFunctionDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.mapreduce.impl.TerminatingMapReduceDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.remove.impl.ExecutableRemoveDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.remove.impl.RemoveWithCollectionDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.remove.impl.RemoveWithQueryDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.remove.impl.TerminatingRemoveDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.update.impl.ExecutableUpdateDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.update.impl.FindAndModifyWithOptionsDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.update.impl.FindAndReplaceWithOptionsDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.update.impl.FindAndReplaceWithProjectionDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.update.impl.TerminatingFindAndModifyDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.update.impl.TerminatingFindAndReplaceDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.update.impl.TerminatingUpdateDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.update.impl.UpdateWithCollectionDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.update.impl.UpdateWithQueryDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.update.impl.UpdateWithUpdateDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.github.cloudyrock.mongock.decorator.util.MethodInvokerImpl;
import com.mongodb.client.ClientSession;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;
import org.springframework.data.mongodb.core.ExecutableFindOperation;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.SessionCallback;
import org.springframework.data.mongodb.core.SessionScoped;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexOperationsProvider;
import org.springframework.data.util.CloseableIterator;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


//TODO generify this and move to testUtils lib for other projects
public class DecoratorUTest {

  private static LockChecker lockChecker;
  private static final Map<Class<?>, DecoratorDefinition> decorators;

  static {
    lockChecker = Mockito.mock(LockChecker.class);
    decorators = new HashMap<>();
    //executableFindOperations
    addDecorator(ExecutableFindOperation.ExecutableFind.class, ExecutableFindDecoratorImpl.class);
    addDecorator(ExecutableFindOperation.FindDistinct.class, FindDistinctDecoratorImpl.class);
    addDecorator(ExecutableFindOperation.FindWithCollection.class, FindWithCollectionDecoratorImpl.class);
    addDecorator(ExecutableFindOperation.FindWithProjection.class, FindWithProjectionDecoratorImpl.class);
    addDecorator(ExecutableFindOperation.FindWithQuery.class, FindWithQueryDecoratorImpl.class);
    addDecorator(ExecutableFindOperation.TerminatingFind.class, TerminatingFindDecoratorImpl.class);
    addDecorator(ExecutableFindOperation.TerminatingFindNear.class, TerminatingFindNearDecoratorImpl.class);
    addDecorator(ExecutableFindOperation.TerminatingDistinct.class, TerminatingDistinctDecoratorImpl.class);
    //executableAggregation
    addDecorator(ExecutableAggregationOperation.AggregationWithCollection.class, AggregationWithCollectionDecoratorImpl.class);
    addDecorator(ExecutableAggregationOperation.AggregationWithAggregation.class, AggregationWithAggregationDecoratorImpl.class);
    addDecorator(ExecutableAggregationOperation.ExecutableAggregation.class, ExecutableAggregationDecoratorImpl.class);
    addDecorator(ExecutableAggregationOperation.TerminatingAggregation.class, TerminatingAggregationDecoratorImpl.class);
    //executableInsert
    addDecorator(ExecutableInsertOperation.ExecutableInsert.class, ExecutableInsertDecoratorImpl.class);
    addDecorator(ExecutableInsertOperation.InsertWithBulkMode.class, InsertWithBulkModeDecoratorImpl.class);
    addDecorator(ExecutableInsertOperation.InsertWithCollection.class, InsertWithCollectionDecoratorImpl.class);
    addDecorator(ExecutableInsertOperation.TerminatingInsert.class, TerminatingInsertDecoratorImpl.class);
    addDecorator(ExecutableInsertOperation.TerminatingBulkInsert.class, TerminatingBulkInsertDecoratorImpl.class);
    //executableRemove
    addDecorator(ExecutableRemoveOperation.ExecutableRemove.class, ExecutableRemoveDecoratorImpl.class);
    addDecorator(ExecutableRemoveOperation.RemoveWithCollection.class, RemoveWithCollectionDecoratorImpl.class);
    addDecorator(ExecutableRemoveOperation.RemoveWithQuery.class, RemoveWithQueryDecoratorImpl.class);
    addDecorator(ExecutableRemoveOperation.TerminatingRemove.class, TerminatingRemoveDecoratorImpl.class);
    //executableUpdate
    addDecorator(ExecutableUpdateOperation.ExecutableUpdate.class, ExecutableUpdateDecoratorImpl.class);
    addDecorator(ExecutableUpdateOperation.UpdateWithQuery.class, UpdateWithQueryDecoratorImpl.class);
    addDecorator(ExecutableUpdateOperation.UpdateWithCollection.class, UpdateWithCollectionDecoratorImpl.class);
    addDecorator(ExecutableUpdateOperation.UpdateWithUpdate.class, UpdateWithUpdateDecoratorImpl.class);
    addDecorator(ExecutableUpdateOperation.FindAndReplaceWithProjection.class, FindAndReplaceWithProjectionDecoratorImpl.class);
    addDecorator(ExecutableUpdateOperation.TerminatingUpdate.class, TerminatingUpdateDecoratorImpl.class);
    addDecorator(ExecutableUpdateOperation.FindAndReplaceWithOptions.class, FindAndReplaceWithOptionsDecoratorImpl.class);
    addDecorator(ExecutableUpdateOperation.TerminatingFindAndModify.class, TerminatingFindAndModifyDecoratorImpl.class);
    addDecorator(ExecutableUpdateOperation.TerminatingFindAndReplace.class, TerminatingFindAndReplaceDecoratorImpl.class);
    addDecorator(ExecutableUpdateOperation.FindAndModifyWithOptions.class, FindAndModifyWithOptionsDecoratorImpl.class);
    //executableMapReduce
    addDecorator(ExecutableMapReduceOperation.ExecutableMapReduce.class, ExecutableMapReduceDecoratorImpl.class);
    addDecorator(ExecutableMapReduceOperation.MapReduceWithOptions.class, MapReduceWithOptionsDecoratorImpl.class);
    addDecorator(ExecutableMapReduceOperation.MapReduceWithCollection.class, MapReduceWithCollectionDecoratorImpl.class);
    addDecorator(ExecutableMapReduceOperation.MapReduceWithReduceFunction.class, MapReduceWithReduceFunctionDecoratorImpl.class);
    addDecorator(ExecutableMapReduceOperation.MapReduceWithMapFunction.class, MapReduceWithMapFunctionDecoratorImpl.class);
    addDecorator(ExecutableMapReduceOperation.MapReduceWithProjection.class, MapReduceWithProjectionDecoratorImpl.class);
    addDecorator(ExecutableMapReduceOperation.MapReduceWithQuery.class, MapReduceWithQueryDecoratorImpl.class);
    addDecorator(ExecutableMapReduceOperation.TerminatingMapReduce.class, TerminatingMapReduceDecoratorImpl.class);
    //basic
    addDecorator(BulkOperations.class, BulkOperationsDecoratorImpl.class);
    addDecorator(ClientSession.class, ClientSessionDecoratorImpl.class, "getPinnedServerAddress", "setPinnedServerAddress", "hasActiveTransaction", "notifyMessageSent", "getTransactionOptions");
    addDecorator(CloseableIterator.class, CloseableIteratorDecoratorImpl.class);
    addDecorator(IndexOperations.class, IndexOperationsDecoratorImpl.class);

//    addDecorator(MongoOperations.class, MongoOperationsDecoratorImpl.class);
    addDecorator(ScriptOperations.class, ScriptOperationsDecoratorImpl.class);
    addDecorator(SessionCallback.class, SessionCallbackDecoratorImpl.class);
    addDecorator(SessionScoped.class, SessionScopedDecoratorImpl.class, "lambda$execute$0");

    //MongockTemplate
    MongockTemplate mongockTemplate = new MongockTemplate(Mockito.mock(MongoTemplate.class), new MethodInvokerImpl(lockChecker));
    addDecorator(MongoOperations.class, MongockTemplate.class, mongockTemplate, "getConverter", "getCollectionName");
    addDecorator(IndexOperationsProvider.class, MongockTemplate.class, mongockTemplate);

    //Not needed as it will be deprecated
//    addDecorator(MongoDbFactory.class, MongoDbFactoryDecoratorImpl.class);
//    addDecorator(MongoTemplate.class, MongoTemplateDecoratorImpl.class);

  }

  private static <T> void addDecorator(Class<T> interfaceType, Class<? extends T> implementingClass, String... noLockGardMethods) {
    decorators.put(interfaceType, new DecoratorDefinition(interfaceType, implementingClass, noLockGardMethods));
  }

  private static <T> void addDecorator(Class<T> interfaceType, Class<? extends T> implementingClass) {
    decorators.put(interfaceType, new DecoratorDefinition(interfaceType, implementingClass));
  }

  private static <T, R extends T> void addDecorator(Class<T> interfaceType, Class<R> implementingClass, R instance, String... noLockGardMethods) {
    decorators.put(interfaceType, new DecoratorDefinition(interfaceType, implementingClass, instance, noLockGardMethods));
  }

  @Test
  public void allMethodsInDecoratorsShouldEnsureLockAndReturnDecoratorIfNotTerminatingOperations() {
    List<DecoratorMethodFailure> failedDecorators = decorators.values()
        .stream()
        .map(decoratorDefinition -> getMethodErrorsFromDecorator(decoratorDefinition, decoratorDefinition.getInterfaceType()))
        .flatMap(Collection::stream)
        .collect(Collectors.toList());

    Assert.assertEquals("Decorator errors should be zero, but it's instead: " + failedDecorators.size(), Collections.emptyList(), failedDecorators);
  }


  private static Collection<DecoratorMethodFailure> getMethodErrorsFromDecorator(DecoratorDefinition decorator, Class interfaceType) {
    Method[] declaredMethods = interfaceType.getDeclaredMethods();
    Collection<DecoratorMethodFailure> methodFailures = Stream.of(declaredMethods)
        .map(method -> getMethodErrorOptional(method, decorator))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());

    Collection<DecoratorMethodFailure> methodFailuresFromHigherInterfaces = Stream.of(interfaceType.getInterfaces())
        .filter(decorators::containsKey)//only interested in interfaces that are decorated
        .map(type -> getMethodErrorsFromDecorator(decorator, type))
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
    methodFailures.addAll(methodFailuresFromHigherInterfaces);
    return methodFailures;
  }

  private static Optional<DecoratorMethodFailure> getMethodErrorOptional(Method method, DecoratorDefinition decorator) {
    try {
      method.setAccessible(true);
      boolean errorReturningDecorator = isErrorReturningDecorator(method, decorator, method.getReturnType());
      boolean errorEnsuringLock = isErrorEnsuringLock(method, decorator);
      return errorEnsuringLock || errorReturningDecorator
          ? Optional.of(new DecoratorMethodFailure(decorator.getImplementingType(), method, errorReturningDecorator, errorEnsuringLock))
          : Optional.empty();
    } catch (Exception ex) {
      return Optional.of(DecoratorMethodFailure.otherError(decorator.getImplementingType(), method, ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName()));
    }
  }

  private static boolean isErrorEnsuringLock(Method method, DecoratorDefinition decorator) throws NoSuchMethodException {

    Collection<Invocation> invocations = Mockito.mockingDetails(lockChecker).getInvocations();
    return !decorator.getNoLockGardMethods().contains(method.getName()) && (invocations.size() != 1 || !invocations.iterator()
        .next()
        .getMethod()
        .equals(LockChecker.class.getMethod("ensureLockDefault")));
  }

  private static boolean isErrorReturningDecorator(Method method, DecoratorDefinition decorator, Class<?> returnType) throws Exception {
    Mockito.reset(lockChecker);
    //method.invoke needs to be executed
    Object result = method.invoke(
        decorator.getInstance().orElseGet(() -> getDecoratorInstance(decorator, new MethodInvokerImpl(lockChecker))),
        getNullParametersFromMethod(method));
    return !Void.TYPE.equals(returnType) && decorators.containsKey(returnType) && !isDecoratorImplementation(result);
  }


  @SuppressWarnings("unchecked")
  private static Object getDecoratorInstance(DecoratorDefinition decorator, MethodInvoker invokerMock) {
    try {
      return decorator.getImplementingType()
          .getConstructor(decorator.getInterfaceType(), MethodInvoker.class)
          .newInstance(Mockito.mock(decorator.getImplementingType()), invokerMock);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static boolean isDecoratorImplementation(Object result) {
    return decorators.values().stream().map(DecoratorDefinition::getImplementingType).anyMatch(result.getClass()::equals);
  }

  @NotNull
  private static Object[] getNullParametersFromMethod(Method method) {
    Class[] parametersType = method.getParameterTypes();
    Object[] parameters = new Object[parametersType.length];
    for (int i = 0; i < parametersType.length; i++) {
      parameters[i] = parametersType[i].cast(null);
    }
    return parameters;
  }


}
