package io.mongock.test.util.decorator;

import io.changock.migration.api.annotations.NonLockGuarded;
import io.changock.migration.api.annotations.NonLockGuardedType;
import io.changock.migration.api.annotations.DecoratorDiverted;
import io.mongock.driver.api.lock.LockManager;
import io.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvokerImpl;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


//TODO validate deocrator is calling the same method in the implementation(same name, same parameters)
public class DecoratorValidator {

  private final Collection<Class> decoratorsWithDifferentNameConvention;
  private final Map<Class, Object> instancesMap;
  private final LockManager lockManager;
  private final DecoratorTestCollection trackedDecorators;
  private final Collection<Class> ignoredTypes;
  private final boolean ignorePrimitives;
  private final boolean ignoreJavaStructures;

  private DecoratorTestCollection decoratorsNextToProcess;

  public DecoratorValidator(DecoratorTestCollection decorators,
                            Collection<Class> ignoredTypes,
                            Collection<Class> decoratorsWithDifferentNameConvention,
                            Map<Class, Object> instancesMap,
                            LockManager lockManager) {
    this(decorators, ignoredTypes, decoratorsWithDifferentNameConvention, instancesMap, true, true, lockManager);
  }

  public DecoratorValidator(DecoratorTestCollection decorators,
                            Collection<Class> ignoredTypes,
                            Collection<Class> decoratorsWithDifferentNameConvention,
                            Map<Class, Object> instancesMap,
                            boolean ignorePrimitives,
                            boolean ignoreJavaStructures,
                            LockManager lockManager) {
    decoratorsNextToProcess = decorators;
    this.ignoredTypes = ignoredTypes;
    this.decoratorsWithDifferentNameConvention = decoratorsWithDifferentNameConvention;
    this.instancesMap = instancesMap;
    this.ignorePrimitives = ignorePrimitives;
    this.ignoreJavaStructures = ignoreJavaStructures;
    this.lockManager = lockManager;
    trackedDecorators = new DecoratorTestCollection();
  }

  public List<DecoratorMethodFailure> checkAndReturnFailedDecorators() {
    List<DecoratorMethodFailure> result = new ArrayList<>();
    while (decoratorsNextToProcess.size() > 0) {
      DecoratorTestCollection decoratorsToProcess = new DecoratorTestCollection(decoratorsNextToProcess);
      trackedDecorators.addAll(decoratorsToProcess);
      decoratorsNextToProcess = new DecoratorTestCollection();
      List<DecoratorMethodFailure> partialResult = decoratorsToProcess
          .stream()
          .map(decoratorDefinition -> getMethodErrorsFromDecorator(decoratorDefinition, decoratorDefinition.getInterfaceType()))
          .flatMap(Collection::stream)
          .collect(Collectors.toList());
      result.addAll(partialResult);
    }
    return result;
  }

  private Collection<DecoratorMethodFailure> getMethodErrorsFromDecorator(DecoratorDefinition decorator, Class<?> interfaceType) {
    Method[] declaredMethods = interfaceType.getDeclaredMethods();
    Collection<DecoratorMethodFailure> methodFailures = Stream.of(declaredMethods)
        .map(method -> getMethodErrorOptional(method, decorator))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());

    methodFailures.addAll(getDecoratorMethodFailuresFromParentInterfaces(decorator, interfaceType));
    return methodFailures;
  }

  private Collection<DecoratorMethodFailure> getDecoratorMethodFailuresFromParentInterfaces(DecoratorDefinition decorator, Class<?> interfaceType) {
    return Stream.of(interfaceType.getInterfaces())
        .map(type -> getMethodErrorsFromDecorator(decorator, type))
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  private Optional<DecoratorMethodFailure> getMethodErrorOptional(Method interfaceMethod, DecoratorDefinition decorator) {
    try {
      Method method = getAccessibleMethod(interfaceMethod, decorator);
      List<NonLockGuardedType> noGuardedLockTypes = getNonLockGuardedTypes(method);
      if (noGuardedLockTypes.contains(NonLockGuardedType.NONE)) {
        return Optional.empty();
      }
      //Note: Keep execution order of next statements
      Mockito.reset(lockManager);
      Object instance = getDecoratorInstance(decorator);
      Object result = executeMethod(method, instance, decorator);
      addResultToValidateIfRequired(result, method);
      return packageResult(decorator, method, result, isErrorEnsuringLock(noGuardedLockTypes), isErrorReturningDecorator(method, result, decorator), !isCallingRightImplMethod(instance, method));
    } catch (Exception ex) {
      return Optional.of(DecoratorMethodFailure.otherError(decorator.getImplementingType(), interfaceMethod, ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName()));
    }
  }

  private List<NonLockGuardedType> getNonLockGuardedTypes(Method method) {
    NonLockGuarded nonLockGuarded = method.getAnnotation(NonLockGuarded.class);
    return nonLockGuarded != null ? Arrays.asList(nonLockGuarded.value()) : Collections.emptyList();
  }

  private void addResultToValidateIfRequired(Object result, Method method) {
    if (shouldNotBeIgnored(method)
        && result != null //if null, it will throw an NullPointerException
        && !trackedDecorators.contains(method.getReturnType(), result.getClass())
        && !decoratorsNextToProcess.contains(method.getReturnType(), result.getClass())
        && shouldReturnObjectBeGuarded(method)) {
      decoratorsNextToProcess.addRawDecorator(method.getReturnType(), result.getClass());
    }
  }

  private boolean isErrorEnsuringLock(List<NonLockGuardedType> noGuardedLockTypes) throws NoSuchMethodException {
    return !(noGuardedLockTypes.contains(NonLockGuardedType.METHOD) || noGuardedLockTypes.contains(NonLockGuardedType.NONE)) && errorInLockInvocations();
  }

  private boolean errorInLockInvocations() throws NoSuchMethodException {
    Collection<Invocation> invocations = Mockito.mockingDetails(lockManager).getInvocations();
    return (invocations.size() != 1 || !invocations.iterator()
        .next()
        .getMethod()
        .equals(LockManager.class.getMethod("ensureLockDefault")));
  }

  private boolean isErrorReturningDecorator(Method method, Object result, DecoratorDefinition decorator) throws Exception {
    return shouldReturnObjectBeGuarded(method) && (result == null || !isDecoratorImplementation(result));
  }

  private Object executeMethod(Method method, Object instance, DecoratorDefinition decorator) throws IllegalAccessException, InvocationTargetException {
    return method.invoke(instance, getDefaultParametersFromMethod(method));
  }


  private boolean isCallingRightImplMethod(Object instance, Method decoratorMethod) throws Exception {
    if (decoratorMethod.isAnnotationPresent(DecoratorDiverted.class)) {
      return true;
    }
    Object implMock = instance.getClass().getMethod("getImpl").invoke(instance);

//    List<Method> methodInvocations = Mockito.mockingDetails(implMock).getInvocations().stream().map(Invocation::getMethod).collect(Collectors.toList());

    return areEquivalent(
        Mockito.mockingDetails(implMock).getInvocations().iterator().next().getMethod(),
        decoratorMethod);
  }

  private boolean areEquivalent(Method implMethod, Method decoratorMethod) {
    for (int i = 0; i < implMethod.getParameterTypes().length; i++) {
      if (!implMethod.getParameterTypes()[i].equals(decoratorMethod.getParameterTypes()[i])) {
        return false;
      }
    }
    String name = implMethod.getName();
    String name1 = decoratorMethod.getName();
    return implMethod.getParameterTypes().length == decoratorMethod.getParameterTypes().length
        && name.equals(name1)
        && decoratorMethod.getReturnType().isAssignableFrom(implMethod.getReturnType());

  }

  @SuppressWarnings("unchecked")
  private Object getDecoratorInstance(DecoratorDefinition decorator) {
    try {
      if (instancesMap.containsKey(decorator.getImplementingType())) {
        Object instance = instancesMap.get(decorator.getImplementingType());
        Mockito.reset(instance.getClass().getMethod("getImpl").invoke(instance));
        return instance;
      } else {
        return decorator.getImplementingType()
            .getConstructor(decorator.getInterfaceType(), LockGuardInvoker.class)
            .newInstance(Mockito.mock(decorator.getInterfaceType()), new LockGuardInvokerImpl(lockManager));
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  //TODO make all the decorator implements DecoratorBase, so we remove: resultClass.getSimpleName().endsWith("DecoratorImpl")
  private boolean isDecoratorImplementation(Object result) {
    Class<?> resultClass = result.getClass();
    return decoratorsWithDifferentNameConvention.contains(resultClass) || resultClass.getSimpleName().endsWith("DecoratorImpl") || DecoratorBase.class.isAssignableFrom(resultClass);
  }

  private static Object[] getDefaultParametersFromMethod(Method method) {
    Class[] parametersType = method.getParameterTypes();
    Object[] parameters = new Object[parametersType.length];
    for (int i = 0; i < parametersType.length; i++) {
      parameters[i] = parametersType[i].isPrimitive() ? getDefaultPrimitiveValue(parametersType[i]) : getDefaultNonPrimitiveValue(parametersType[i]);
    }
    return parameters;
  }

  private static Object getDefaultNonPrimitiveValue(Class aClass) {
    return aClass.isInterface() ? Mockito.mock(aClass) : aClass.cast(null);
  }

  private static Object getDefaultPrimitiveValue(Class clazz) {
    if (clazz == Integer.TYPE) return Integer.MIN_VALUE;
    if (clazz == Long.TYPE) return Long.MIN_VALUE;
    if (clazz == Boolean.TYPE) return false;
    if (clazz == Byte.TYPE) return Byte.MIN_VALUE;
    if (clazz == Character.TYPE) return 'c';
    if (clazz == Float.TYPE) return Float.MIN_VALUE;
    if (clazz == Double.TYPE) return Double.MIN_VALUE;
    if (clazz == Short.TYPE) return Short.MIN_VALUE;
    return null;
  }

  private boolean shouldReturnObjectBeGuarded(Method method) {
    List<NonLockGuardedType> noGuardedLockTypes = getNonLockGuardedTypes(method);

    return !Void.TYPE.equals(method.getReturnType())
        && shouldNotBeIgnored(method)
        && !noGuardedLockTypes.contains(NonLockGuardedType.RETURN)
        && !noGuardedLockTypes.contains(NonLockGuardedType.NONE)
        && !method.getReturnType().isAnnotationPresent(NonLockGuarded.class);
  }

  private boolean shouldNotBeIgnored(Method method) {
    Class<?> returnType = method.getReturnType();
    return !(ignoredTypes.contains(returnType) || isPrimitiveIgnored(returnType) || isJavaStructureIgnored(returnType));
  }

  private boolean isPrimitiveIgnored(Class c) {
    return ignorePrimitives && (c.isPrimitive() || String.class.equals(c));
  }

  private boolean isJavaStructureIgnored(Class c) {
    return ignoreJavaStructures && javaStructuresTypes.contains(c);
  }

  private Optional<DecoratorMethodFailure> packageResult(DecoratorDefinition decorator,
                                                         Method method,
                                                         Object result,
                                                         boolean errorEnsuringLock,
                                                         boolean errorReturningDecorator,
                                                         boolean errorCallingRightImplMethod) {
    String otherErrorDetail = errorCallingRightImplMethod
        ? "not calling the right impl method"
        : shouldReturnObjectBeGuarded(method) && result == null ? "returns null" : "";
    return errorEnsuringLock || errorReturningDecorator || errorCallingRightImplMethod || !otherErrorDetail.isEmpty()
        ? Optional.of(new DecoratorMethodFailure(decorator.getImplementingType(), method, errorReturningDecorator, errorEnsuringLock, otherErrorDetail))
        : Optional.empty();
  }

  @SuppressWarnings("unchecked")
  private Method getAccessibleMethod(Method interfaceMethod, DecoratorDefinition decorator) throws NoSuchMethodException {
    Method method;
    method = decorator.getImplementingType().getMethod(interfaceMethod.getName(), interfaceMethod.getParameterTypes());
    method.setAccessible(true);
    return method;
  }

  //TODO keep adding more on demand
  private final static Collection<Class> javaStructuresTypes = Arrays.asList(
      List.class,
      Collection.class,
      Map.class,
      HashMap.class,
      Set.class,
      HashSet.class,
      Stream.class,
      Object.class,
      Class.class
  );
}
