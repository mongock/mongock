package io.mongock.runner.springboot.base;

import io.mongock.runner.springboot.base.util.Application;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ConfigurableApplicationContext;

public class MongockInitializingBeanRunnerITest {

  @Test
  public void ShouldApplicationRunnerBean_WhenSpringApplication_IfRunnerTypeIsApplicationRunner() {
    try (ConfigurableApplicationContext ctx = Application.getSpringAppBuilder()
        .properties("mongock.runner-type=ApplicationRunner").run()) {

      Assertions.assertNotNull(ctx.getBean(MongockApplicationRunner.class));
      Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(MongockInitializingBeanRunner.class));
    }
  }

  @Test
  public void ShouldApplicationRunnerBean_WhenSpringApplication_IfDefaultRunnerTypeI() {

    try (ConfigurableApplicationContext ctx = Application.getSpringAppBuilder().properties().run()) {

      Assertions.assertNotNull(ctx.getBean(MongockApplicationRunner.class));
      Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(MongockInitializingBeanRunner.class));
    }
  }

  @Test
  public void ShouldInitializingBeanRunnerBean_WhenSpringApplication_IfRunnerTypeIsInitializingBeanRunner() {

    try (ConfigurableApplicationContext ctx = Application.getSpringAppBuilder()
        .properties("mongock.runner-type=InitializingBean").run()) {
      Assertions.assertNotNull(ctx.getBean(MongockInitializingBeanRunner.class));
      Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(MongockApplicationRunner.class));
    }
  }

  @Test
  public void ShouldInitializingBeanRunnerBean_WhenSpringApplication_IfRunnerTypeLowerCase() {

    try (ConfigurableApplicationContext ctx = Application.getSpringAppBuilder()
        .properties("mongock.runner-type=initializingbean").run()) {
      Assertions.assertNotNull(ctx.getBean(MongockInitializingBeanRunner.class));
      Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(MongockApplicationRunner.class));
    }
  }

  @Test
  public void ShouldNotInjectMongockRunner_WhenSpringApplication_IfEnabledIsFalse() {

    try (ConfigurableApplicationContext ctx = Application.getSpringAppBuilder()
        .properties("mongock.enabled=false").run()) {
      Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(MongockApplicationRunner.class));
      Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> ctx.getBean(MongockInitializingBeanRunner.class));
    }
  }

  //  @ParameterizedTest
//  @MethodSource("provider")
//  void shouldThrowExceptionWhenScanPackageNotSpecified(String mongoVersion, boolean sleuthEnabled) {
//    Exception ex = assertThrows(
//            BeanCreationException.class,
//            () -> RuntimeTestUtil.startSpringAppWithMongoDbVersionAndNoPackage(mongoVersion, sleuthEnabled));
//    Throwable BeanInstantiationEx = ex.getCause();
//    assertEquals(BeanInstantiationException.class, BeanInstantiationEx.getClass());
//    Throwable mongockEx = BeanInstantiationEx.getCause();
//    assertEquals(MongockException.class, mongockEx.getClass());
//    assertEquals("Scan package for changeLogs is not set: use appropriate setter", mongockEx.getMessage());
//  }
}
