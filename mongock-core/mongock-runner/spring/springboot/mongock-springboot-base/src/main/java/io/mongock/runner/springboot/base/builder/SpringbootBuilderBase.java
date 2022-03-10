package io.mongock.runner.springboot.base.builder;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.runner.core.builder.BuilderType;
import io.mongock.runner.core.builder.RunnerBuilderBase;
import io.mongock.runner.core.event.EventPublisher;
import io.mongock.runner.core.executor.ExecutorBuilder;
import io.mongock.runner.core.executor.changelog.ChangeLogServiceBase;
import io.mongock.runner.core.executor.dependency.DependencyContext;
import io.mongock.runner.core.executor.dependency.DependencyManagerWithContext;
import io.mongock.runner.spring.base.context.SpringDependencyContext;
import io.mongock.runner.spring.base.events.SpringMigrationFailureEvent;
import io.mongock.runner.spring.base.events.SpringMigrationStartedEvent;
import io.mongock.runner.spring.base.events.SpringMigrationSuccessEvent;
import io.mongock.runner.spring.base.util.ProfileUtil;
import io.mongock.runner.springboot.base.MongockApplicationRunner;
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner;
import io.mongock.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import javax.inject.Named;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static io.mongock.utils.Constants.CLI_PROFILE;

public abstract class SpringbootBuilderBase<
    SELF extends SpringbootBuilderBase<SELF, CONFIG>,
    CONFIG extends MongockConfiguration>
    extends RunnerBuilderBase<SELF, CONFIG> {

  private static final String DEFAULT_PROFILE = "default";

  protected SpringbootBuilderBase(BuilderType builderType,
                                  ExecutorBuilder<CONFIG> executorBuilder,
                                  ChangeLogServiceBase changeLogService,
                                  CONFIG config) {
    super(builderType, executorBuilder, changeLogService, new DependencyManagerWithContext(), config);
    parameterNameFunction = buildParameterNameFunctionForSpring();
  }

  private static List<String> getActiveProfilesFromContext(ApplicationContext springContext) {
    Environment springEnvironment = springContext.getEnvironment();
    return springEnvironment != null && CollectionUtils.isNotNullOrEmpty(springEnvironment.getActiveProfiles())
        ? Arrays.asList(springEnvironment.getActiveProfiles())
        : Collections.singletonList(DEFAULT_PROFILE);
  }

  private static Function<Parameter, String> buildParameterNameFunctionForSpring() {
    return parameter -> {
      String name = parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;
      if (name == null) {
        name = parameter.isAnnotationPresent(Qualifier.class) ? parameter.getAnnotation(Qualifier.class).value() : null;
      }
      return name;
    };
  }


  ///////////////////////////////////////////////////
  // Build methods
  ///////////////////////////////////////////////////

  public SELF setSpringContext(ApplicationContext springContext) {
    (getDependencyManager()).setContext(new SpringDependencyContext(springContext));
    setEventPublisherInternal(springContext);
    return getInstance();
  }

  /**
   * Deprecated. Just use setSpringContext
   */
  @Deprecated
  public SELF setEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    setEventPublisherInternal(applicationEventPublisher);
    return getInstance();
  }

  private void setEventPublisherInternal(ApplicationEventPublisher applicationEventPublisher) {
    if (applicationEventPublisher == null) {
      throw new MongockException("EventPublisher cannot e null");
    }
    this.eventPublisher = new EventPublisher(
        () -> applicationEventPublisher.publishEvent(new SpringMigrationStartedEvent(this)),
        result -> applicationEventPublisher.publishEvent(new SpringMigrationSuccessEvent(this, result)),
        result -> applicationEventPublisher.publishEvent(new SpringMigrationFailureEvent(this, result))
    );
  }

  public MongockApplicationRunner buildApplicationRunner() {
    return new MongockApplicationRunner(buildRunner());
  }

  public MongockInitializingBeanRunner buildInitializingBeanRunner() {
    return new MongockInitializingBeanRunner(buildRunner());
  }


  @Override
  protected void beforeBuildRunner(ConnectionDriver driver) {
    super.beforeBuildRunner(driver);
    DependencyContext dependencyContext = getDependencyManager().getDependencyContext();
    Environment environment = ((SpringDependencyContext) dependencyContext).getSpringContext().getEnvironment();
    //if cli active(mongock-cli-profile), the runner needs to be disabled
    if(environment.getActiveProfiles()!= null && Stream.of(environment.getActiveProfiles()).anyMatch(CLI_PROFILE::equalsIgnoreCase)) {
      CONFIG config = getConfig();
      config.setEnabled(false);
      setConfig(config);
    }
  }

  @Override
  protected Function<AnnotatedElement, Boolean> getAnnotationFilter() {
    DependencyContext dependencyContext = getDependencyManager().getDependencyContext();
    ApplicationContext springContext = ((SpringDependencyContext) dependencyContext).getSpringContext();
    return annotated -> ProfileUtil.matchesActiveSpringProfile(
        getActiveProfilesFromContext(springContext),
        Profile.class,
        annotated,
        (AnnotatedElement element) -> element.getAnnotation(Profile.class).value());
  }

  @Override
  protected void validateConfigurationAndInjections(ConnectionDriver driver) {
    super.validateConfigurationAndInjections(driver);
    if (!(getDependencyManager()).isContextPresent()) {
      throw new MongockException("ApplicationContext from Spring must be injected to Builder");
    }
  }

  public DependencyManagerWithContext getDependencyManager() {
    return (DependencyManagerWithContext) dependencyManager;
  }

}
