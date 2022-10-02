package io.mongock.runner.core.executor;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.runner.core.executor.changelog.ChangeLogRuntime;
import io.mongock.runner.core.executor.changelog.ChangeLogService;
import io.mongock.runner.core.executor.operation.Operation;
import io.mongock.runner.core.executor.operation.migrate.MigrateAllExecutor;
import io.mongock.runner.core.executor.operation.migrate.MigrateAllOperation;
import io.mongock.runner.core.executor.system.SystemUpdateExecutor;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


public class ExecutorBuilderTest {
  
  private static final Function<AnnotatedElement, Boolean> DEFAULT_ANNOTATION_FILTER = e -> true;
  
  private ConnectionDriver driver;
  private ChangeLogService changeLogService;
  private ChangeLogRuntime changeLogRuntime;
  
  @BeforeEach
  public void beforeEach() {
    driver = mock(ConnectionDriver.class);
    changeLogService = mock(ChangeLogService.class);
    changeLogRuntime = mock(ChangeLogRuntime.class);
  }
  
  @Test
  public void shouldThrowException_whenBuildOperationExecutor_ifNoOperationProvided() {
    
    // given
    ExecutorBuilder builder = getExecutorBuilder();
    
    // then
    MongockException ex = assertThrows(MongockException.class, () -> builder.buildOperationExecutor());
    assertEquals("operation cannot be null", ex.getMessage());
  }
  
  @ParameterizedTest
  @MethodSource("provideParameters_validateCommons")
  public void shouldThrowException_whenBuildExecutor_ifNoExecutionIdProvided(boolean systemExecutor, Operation operation) {
    
    // given
    MongockConfiguration config = new MongockConfiguration();
    config.setMigrationScanPackage(Collections.singletonList("test.package"));
    
    ExecutorBuilderDefault builder = getExecutorBuilder();
    builder.setOperation(systemExecutor ? null : operation)
           .setDriver(driver)
           .setConfig(config)
           .setChangeLogService(changeLogService)
           .setChangeLogRuntime(changeLogRuntime)
           .setAnnotationFilter(DEFAULT_ANNOTATION_FILTER);
    
    // then
    MongockException ex = assertThrows(MongockException.class, 
            systemExecutor ? () -> builder.buildSystemExecutor() : () -> builder.buildOperationExecutor());
    assertEquals("executionId cannot be null or empty", ex.getMessage());
  }
  
  @ParameterizedTest
  @MethodSource("provideParameters_validateCommons")
  public void shouldThrowException_whenBuildExecutor_ifNoDriverProvided(boolean systemExecutor, Operation operation) {
    
    // given
    MongockConfiguration config = new MongockConfiguration();
    config.setMigrationScanPackage(Collections.singletonList("test.package"));
    
    ExecutorBuilderDefault builder = getExecutorBuilder();
    builder.setOperation(systemExecutor ? null : operation)
           .setExecutionId("test-execution")
           .setConfig(config)
           .setChangeLogService(changeLogService)
           .setChangeLogRuntime(changeLogRuntime)
           .setAnnotationFilter(DEFAULT_ANNOTATION_FILTER);
    
    // then
    MongockException ex = assertThrows(MongockException.class, 
            systemExecutor ? () -> builder.buildSystemExecutor() : () -> builder.buildOperationExecutor());
    assertEquals("driver cannot be null", ex.getMessage());
  }
  
  @ParameterizedTest
  @MethodSource("provideParameters_validateCommons")
  public void shouldThrowException_whenBuildExecutor_ifNoConfigProvided(boolean systemExecutor, Operation operation) {
    
    // given
    ExecutorBuilderDefault builder = getExecutorBuilder();
    builder.setOperation(systemExecutor ? null : operation)
           .setExecutionId("test-execution")
           .setDriver(driver)
           .setChangeLogService(changeLogService)
           .setChangeLogRuntime(changeLogRuntime)
           .setAnnotationFilter(DEFAULT_ANNOTATION_FILTER);
    
    // then
    MongockException ex = assertThrows(MongockException.class, 
            systemExecutor ? () -> builder.buildSystemExecutor() : () -> builder.buildOperationExecutor());
    assertEquals("config cannot be null", ex.getMessage());
  }
  
  @ParameterizedTest
  @MethodSource("provideParameters_validateChangeLogService")
  public void shouldThrowException_whenBuildMigrateAllOperationExecutor_ifNoChangeLogServiceProvided(boolean systemExecutor, Operation operation) {
    
    // given
    MongockConfiguration config = new MongockConfiguration();
    config.setMigrationScanPackage(Collections.singletonList("test.package"));
    
    ExecutorBuilderDefault builder = getExecutorBuilder();
    builder.setOperation(systemExecutor ? null : operation)
           .setExecutionId("test-execution")
           .setDriver(driver)
           .setConfig(config)
           .setChangeLogRuntime(changeLogRuntime)
           .setAnnotationFilter(DEFAULT_ANNOTATION_FILTER);
    
    // then
    MongockException ex = assertThrows(MongockException.class, 
            systemExecutor ? () -> builder.buildSystemExecutor() : () -> builder.buildOperationExecutor());
    assertEquals("changeLogService cannot be null", ex.getMessage());
  }
  
  @ParameterizedTest
  @MethodSource("provideParameters_validateChangeLogRuntime")
  public void shouldThrowException_whenBuildMigrateAllOperationExecutor_ifNoChangeLogRuntimeProvided(boolean systemExecutor, Operation operation) {
    
    // given
    MongockConfiguration config = new MongockConfiguration();
    config.setMigrationScanPackage(Collections.singletonList("test.package"));
    
    ExecutorBuilderDefault builder = getExecutorBuilder();
    builder.setOperation(systemExecutor ? null : operation)
           .setExecutionId("test-execution")
           .setDriver(driver)
           .setConfig(config)
           .setChangeLogService(changeLogService)
           .setAnnotationFilter(DEFAULT_ANNOTATION_FILTER);
    
    // then
    MongockException ex = assertThrows(MongockException.class, 
            systemExecutor ? () -> builder.buildSystemExecutor() : () -> builder.buildOperationExecutor());
    assertEquals("changeLogRuntime cannot be null", ex.getMessage());
  }
  
  @ParameterizedTest
  @MethodSource("provideParameters_validateScanPackage")
  public void shouldThrowException_whenBuildMigrateAllOperationExecutor_ifNoScanPackageProvided(boolean systemExecutor, Operation operation) {
    
    // given
    MongockConfiguration config = new MongockConfiguration();
    config.setMigrationScanPackage(null);
    
    ExecutorBuilderDefault builder = getExecutorBuilder();
    builder.setOperation(systemExecutor ? null : operation)
           .setExecutionId("test-execution")
           .setDriver(driver)
           .setConfig(config)
           .setChangeLogService(changeLogService)
           .setChangeLogRuntime(changeLogRuntime)
           .setAnnotationFilter(DEFAULT_ANNOTATION_FILTER);
    
    // then
    MongockException ex = assertThrows(MongockException.class, () -> builder.buildOperationExecutor());
    assertEquals("Scan package for changeLogs is not set: use appropriate setter", ex.getMessage());
  }
  
  @Test
  public void shouldProduceMigrateAllExecutor_whenBuildMigrateAllOperationExecutor() {
    
    // given
    Operation operation = new MigrateAllOperation();
    MongockConfiguration config = new MongockConfiguration();
    config.setMigrationScanPackage(Collections.singletonList("test.package"));
    
    ExecutorBuilderDefault builder = getExecutorBuilder();
    builder.setOperation(operation)
           .setExecutionId("test-execution")
           .setDriver(driver)
           .setConfig(config)
           .setChangeLogService(changeLogService)
           .setChangeLogRuntime(changeLogRuntime)
           .setAnnotationFilter(DEFAULT_ANNOTATION_FILTER);
    
    // then
    Executor executor = builder.buildOperationExecutor();
    
    assertEquals(MigrateAllExecutor.class, executor.getClass());
  }
  
  @ParameterizedTest
  @MethodSource("provideParameters_validateProducedExecutor")
  public void shouldProduceCorrectExecutor_whenBuildOperationExecutor(Operation operation, Class expectedExecutorClass) {
    
    // given
    MongockConfiguration config = new MongockConfiguration();
    config.setMigrationScanPackage(Collections.singletonList("test.package"));
    
    ExecutorBuilderDefault builder = getExecutorBuilder();
    builder.setOperation(operation)
           .setExecutionId("test-execution")
           .setDriver(driver)
           .setConfig(config)
           .setChangeLogService(changeLogService)
           .setChangeLogRuntime(changeLogRuntime)
           .setAnnotationFilter(DEFAULT_ANNOTATION_FILTER);
    
    // then
    Executor executor = builder.buildOperationExecutor();
    
    assertEquals(expectedExecutorClass, executor.getClass());
  }
  
  @Test
  public void shouldProduceSystemUpdateExecutor_whenBuildSystemExecutor() {
    
    // given
    MongockConfiguration config = new MongockConfiguration();
    config.setMigrationScanPackage(Collections.singletonList("test.package"));
    
    ExecutorBuilderDefault builder = getExecutorBuilder();
    builder.setExecutionId("test-execution")
           .setDriver(driver)
           .setConfig(config)
           .setChangeLogService(changeLogService)
           .setChangeLogRuntime(changeLogRuntime)
           .setAnnotationFilter(DEFAULT_ANNOTATION_FILTER);
    
    // then
    Executor executor = builder.buildSystemExecutor();
    
    assertEquals(SystemUpdateExecutor.class, executor.getClass());
  }
  
  
  
  private static Stream<Arguments> provideParameters_validateCommons() {
    return Stream.of(
      Arguments.of(true, null),
      Arguments.of(false, new MigrateAllOperation())
    );
  }
  
  private static Stream<Arguments> provideParameters_validateChangeLogService() {
    return Stream.of(
      Arguments.of(false, new MigrateAllOperation())
    );
  }
  
  private static Stream<Arguments> provideParameters_validateChangeLogRuntime() {
    return Stream.of(
      Arguments.of(false, new MigrateAllOperation())
    );
  }
  
  private static Stream<Arguments> provideParameters_validateScanPackage() {
    return Stream.of(
      Arguments.of(false, new MigrateAllOperation())
    );
  }
  
  private static Stream<Arguments> provideParameters_validateProducedExecutor() {
    return Stream.of(
      Arguments.of(new MigrateAllOperation(), MigrateAllExecutor.class)
    );
  }
  
  private ExecutorBuilderDefault getExecutorBuilder() {
    return new ExecutorBuilderDefault();
  }
}
