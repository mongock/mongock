package io.mongock.runner.springboot.util;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

/**
 * Mock for Spring environment
 *
 * @since 2014-09-19
 */
public class EnvironmentMock implements Environment {

  private String[] activeProfiles;

  public EnvironmentMock(String... activeProfiles) {
    this.activeProfiles = activeProfiles;
  }

  @Override
  public String[] getActiveProfiles() {
    return this.activeProfiles;
  }

  @Override
  public String[] getDefaultProfiles() {
    return new String[0];
  }

  @Override
  public boolean acceptsProfiles(String... strings) {
    return false;
  }

  @Override
  public boolean acceptsProfiles(Profiles profiles) {
    return false;
  }

  @Override
  public boolean containsProperty(String s) {
    return false;
  }

  @Override
  public String getProperty(String s) {
    return null;
  }

  @Override
  public String getProperty(String s, String s2) {
    return null;
  }

  @Override
  public <T> T getProperty(String s, Class<T> tClass) {
    return null;
  }

  @Override
  public <T> T getProperty(String s, Class<T> tClass, T t) {
    return null;
  }

  @Override
  public String getRequiredProperty(String s) throws IllegalStateException {
    return null;
  }

  @Override
  public <T> T getRequiredProperty(String s, Class<T> tClass) throws IllegalStateException {
    return null;
  }

  @Override
  public String resolvePlaceholders(String s) {
    return null;
  }

  @Override
  public String resolveRequiredPlaceholders(String s) throws IllegalArgumentException {
    return null;
  }
}
