package com.github.cloudyrock.mongock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;

public class SpringChangeServiceTest {

  private Environment environment;

  @Before
  public void before() {
    environment = Mockito.mock(Environment.class);
  }


  @Test
  public void shouldRunChangeSets_WhenDefaultProfileEnv_IfOnlyNegativeProfileInAnnotationOrNoAnnotation() {
    Mockito.when(environment.getActiveProfiles()).thenReturn(new String[]{});
    SpringChangeService service = new SpringChangeService();
    service.setEnvironment(environment);
    assertEquals(service.fetchChangeSets(ChangeLogClass.class).size(), 4);
  }


  @Test
  public void shouldRunChangeSets_When1NonDefaultProfileEnv_IfPresentInAnnotationOrNoAnnotationOrOnlyOtherNegatives() {
    Mockito.when(environment.getActiveProfiles()).thenReturn(new String[]{"test"});
    SpringChangeService service = new SpringChangeService();
    service.setEnvironment(environment);
    assertEquals(service.fetchChangeSets(ChangeLogClass.class).size(), 4);
  }


  @Test
  public void shouldRunChangeSets_WhenMultipleNonDefaultProfileEnv_IfPresentInAnnotationOrNoAnnotationOrOnlyOtherNegatives() {
    Mockito.when(environment.getActiveProfiles()).thenReturn(new String[]{"test", "dataset"});
    SpringChangeService service = new SpringChangeService();
    service.setEnvironment(environment);
    assertEquals(service.fetchChangeSets(ChangeLogClass.class).size(), 5);
  }

}

@ChangeLog
class ChangeLogClass {

  @ChangeSet(id="noProfiles", author = "testAuthor", order = "01")
  public void noProfile() {
  }


  @Profile({"!test"})
  @ChangeSet(id="oneNegativeProfile", author = "testAuthor", order = "01")
  public void oneNegativeProfile() {
  }

  @Profile({"!dataset"})
  @ChangeSet(id="oneOtherNegativeProfile", author = "testAuthor", order = "01")
  public void oneOtherNegativeProfile() {
  }

  @Profile({"!test", "!dataset"})
  @ChangeSet(id="multipleNegativeProfiles", author = "testAuthor", order = "01")
  public void multipleNegativeProfiles() {
  }


  @Profile({"test"})
  @ChangeSet(id="onePositiveProfile", author = "testAuthor", order = "01")
  public void onePositiveProfile() {
  }


  @Profile({"dataset"})
  @ChangeSet(id="oneOtherPositiveProfile", author = "testAuthor", order = "01")
  public void oneOtherPositiveProfile() {
  }

  @Profile({"dataset"})
  @ChangeSet(id="oneOtherPositiveProfile2", author = "testAuthor", order = "01")
  public void oneOtherPositiveProfile2() {
  }


  @Profile({"test", "dataset"})
  @ChangeSet(id="multiplePositiveProfiles", author = "testAuthor", order = "01")
  public void multiplePositiveProfiles() {
  }

}
