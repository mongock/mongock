package com.github.cloudyrock.spring.v5;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;

@TestPropertySource
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MongockContext.class, MongockConfiguration.class})
public class MongockContextTest {

  @Autowired
  ApplicationContext applicationContext;

  @Value("${mongock.enabled}")
  private boolean mongockEnabled;

  @Test
  public void isEnabled() {
    assertFalse(mongockEnabled);
  }

  @Test(expected = NoSuchBeanDefinitionException.class)
  public void applicationRunner() {
    applicationContext.getBean(MongockSpring5.MongockApplicationRunner.class);
  }

  @Test(expected = NoSuchBeanDefinitionException.class)
  public void initializingBean() {
    applicationContext.getBean(MongockSpring5.MongockInitializingBeanRunner.class);
  }

  @Test(expected = NoSuchBeanDefinitionException.class)
  public void dataV2CoreContext() {
    applicationContext.getBean(MongockSpringDataV2CoreContext.class);
  }

  @Test(expected = NoSuchBeanDefinitionException.class)
  public void dataV3CoreContext() {
    applicationContext.getBean(MongockSpringDataV3CoreContext.class);
  }

}
