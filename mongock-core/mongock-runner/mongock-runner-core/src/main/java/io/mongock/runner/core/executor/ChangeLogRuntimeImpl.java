package io.mongock.runner.core.executor;

import io.changock.migration.api.annotations.NonLockGuarded;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.ChangeUnitConstructor;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.common.DependencyInjectionException;
import io.mongock.driver.api.driver.ChangeSetDependency;
import io.mongock.driver.api.lock.LockManager;
import io.mongock.driver.api.lock.guard.proxy.LockGuardProxyFactory;
import io.mongock.runner.core.executor.changelog.ChangeLogRuntime;
import io.mongock.runner.core.executor.dependency.DependencyManager;
import io.mongock.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ChangeLogRuntimeImpl implements ChangeLogRuntime {
  private static final Logger logger = LoggerFactory.getLogger(ChangeLogRuntimeImpl.class);


  private static final Function<Class<?>, Object> DEFAULT_FUNC_FOR_ANNOTATIONS = changeLogClass -> {
    try {
      return changeLogClass.getConstructor().newInstance();
    } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
      throw new MongockException(e);
    }
  };

  private final Function<Class<?>, Object> instantiatorForAnnotations;//todo remove this
  private final DependencyManager dependencyManager;
  private final Function<Parameter, String> parameterNameProvider;
  private final Set<Class<?>> nonProxyableTypes;

  public ChangeLogRuntimeImpl(DependencyManager dependencyManager, Function<Parameter, String> parameterNameProvider, List<Class<?>> nonProxyableTypes) {
    this(DEFAULT_FUNC_FOR_ANNOTATIONS, dependencyManager, parameterNameProvider, nonProxyableTypes);
  }

  @Deprecated
  public ChangeLogRuntimeImpl(Function<Class<?>, Object> instantiatorForAnnotations,
                              DependencyManager dependencyManager,
                              Function<Parameter, String> parameterNameProvider,
                              List<Class<?>> nonProxyableTypes) {
    this.instantiatorForAnnotations = instantiatorForAnnotations != null ? instantiatorForAnnotations : DEFAULT_FUNC_FOR_ANNOTATIONS;
    this.dependencyManager = dependencyManager;
    this.parameterNameProvider = parameterNameProvider;
    this.nonProxyableTypes = new HashSet<>(nonProxyableTypes);
  }

  @Override
  public void initialize(LockManager lockManager) {
    dependencyManager.setLockGuardProxyFactory(new LockGuardProxyFactory(lockManager));
  }

  @Override
  public void updateDriverDependencies(Set<ChangeSetDependency> dependencies) {
    dependencyManager.addDriverDependencies(dependencies);
    dependencyManager.runValidation();
  }

  @Override
  public void runChangeSet(Object changeLogInstance, Method changeSetMethod) throws IllegalAccessException, InvocationTargetException {
    List<Object> invokationParameters = getInvokationParameters(changeSetMethod);
    LogUtils.logMethodWithArguments(logger, changeSetMethod.getName(), invokationParameters);
    changeSetMethod.invoke(changeLogInstance, invokationParameters.toArray());
  }

  @Override
  public Object getInstance(Class<?> type) {
    if (!type.isAnnotationPresent(ChangeUnit.class)) {
      //TODO Legacy: we should able to remove this. Else block should handle no parameters too
      return instantiatorForAnnotations.apply(type);
    } else {
      Constructor<?> constructor = getConstructor(type);
      List<Object> invokationParameters = getInvokationParameters(constructor);
      LogUtils.logMethodWithArguments(logger, constructor.getName(), invokationParameters);
      try {
        return constructor.newInstance(invokationParameters.toArray());
      } catch (Exception e) {
        throw new MongockException(e);
      }
    }
  }


  private List<Object> getInvokationParameters(Executable executable) {
    Class<?>[] parameterTypes = executable.getParameterTypes();
    Parameter[] parameters = executable.getParameters();
    List<Object> invokationParameters = new ArrayList<>(parameterTypes.length);
    for (int paramIndex = 0; paramIndex < parameterTypes.length; paramIndex++) {
      invokationParameters.add(getParameter(parameterTypes[paramIndex], parameters[paramIndex]));
    }
    return invokationParameters;
  }


  private Object getParameter(Class<?> parameterType, Parameter parameter) {
    String name = getParameterName(parameter);
    boolean makeItProxy = !parameterType.isAnnotationPresent(NonLockGuarded.class)
        && !parameter.isAnnotationPresent(NonLockGuarded.class)
        && !nonProxyableTypes.contains(parameterType);
    return dependencyManager
        .getDependency(parameterType, name, makeItProxy)
        .orElseThrow(() -> new DependencyInjectionException(parameterType, name));
  }

  private String getParameterName(Parameter parameter) {
    return parameterNameProvider.apply(parameter);
  }


  private Constructor<?> getConstructor(Class<?> type) {
    return findChangeUnitConstructor(type)
        .orElseGet(() -> findDefaultConstructor(type));
  }

  private Constructor<?> findDefaultConstructor(Class<?> type) {
    Supplier<Stream<Constructor<?>>> constructorSupplier = () -> Arrays.stream(type.getConstructors());
    if (constructorSupplier.get().count() > 1) {
      logger.warn("Mongock found multiple constructors for changeUnit[{}]. " +
          "It's recommended to annotate the one you want Mongock to use with @ChangeUnitConstructor. " +
          "FROM VERSION 6 THIS WILL CAUSE AN ERROR ", type.getName());
    }
    return constructorSupplier.get()
        .findFirst()
        .orElseThrow(() -> new MongockException("Mongock cannot find a valid constructor for changeUnit[%s]", type.getName()));
  }

  private Optional<Constructor<?>> findChangeUnitConstructor(Class<?> type) {
    Supplier<Stream<Constructor<?>>> changeUnitConstructorsSupplier = () -> Arrays.stream(type.getConstructors())
        .filter(constructor -> constructor.isAnnotationPresent(ChangeUnitConstructor.class));
    if (changeUnitConstructorsSupplier.get().count() > 1) {
      throw new MongockException("Found multiple constructors for changeUnit[%s] without annotation @ChangeUnitConstructor." +
          " Annotate the one you want Mongock to use to instantiate your changeUnit", type.getName());
    }
    return changeUnitConstructorsSupplier.get().findFirst();
  }
}
