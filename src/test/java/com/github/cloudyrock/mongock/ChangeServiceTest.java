package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.test.changelogs.AnotherMongockTestResource;
import com.github.cloudyrock.mongock.test.changelogs.MongockTestResource;
import com.github.cloudyrock.mongock.utils.ChangeLogWithDuplicate;
import junit.framework.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author lstolowski
 * @since 27/07/2014
 */
public class ChangeServiceTest {

  @Test
  public void shouldFindChangeLogClasses() {
    // given
    String scanPackage = MongockTestResource.class.getPackage().getName();
    ChangeService service = new ChangeService();
    service.setChangeLogsBasePackage(scanPackage);
    // when
    List<Class<?>> foundClasses = service.fetchChangeLogs();
    // then
    assertTrue(foundClasses != null && foundClasses.size() > 0);
  }

  @Test
  public void shouldFindChangeSetMethods() throws MongockException {
    // given
    String scanPackage = MongockTestResource.class.getPackage().getName();
    ChangeService service = new ChangeService();
    service.setChangeLogsBasePackage(scanPackage);

    // when
    List<Method> foundMethods = service.fetchChangeSets(MongockTestResource.class);

    // then
    assertTrue(foundMethods != null && foundMethods.size() == 5);
  }

  @Test
  public void shouldFindAnotherChangeSetMethods() throws MongockException {
    // given
    String scanPackage = MongockTestResource.class.getPackage().getName();
    ChangeService service = new ChangeService();
    service.setChangeLogsBasePackage(scanPackage);

    // when
    List<Method> foundMethods = service.fetchChangeSets(AnotherMongockTestResource.class);

    // then
    assertTrue(foundMethods != null && foundMethods.size() == 6);
  }

  @Test
  public void shouldFindIsRunAlwaysMethod() throws MongockException {
    // given
    String scanPackage = MongockTestResource.class.getPackage().getName();
    ChangeService service = new ChangeService();
    service.setChangeLogsBasePackage(scanPackage);

    // when
    List<Method> foundMethods = service.fetchChangeSets(AnotherMongockTestResource.class);
    // then
    for (Method foundMethod : foundMethods) {
      if (foundMethod.getName().equals("testChangeSetWithAlways")) {
        assertTrue(service.isRunAlwaysChangeSet(foundMethod));
      } else {
        assertFalse(service.isRunAlwaysChangeSet(foundMethod));
      }
    }
  }

  @Test
  public void shouldCreateEntry() throws MongockException {

    // given
    String scanPackage = MongockTestResource.class.getPackage().getName();
    ChangeService service = new ChangeService();
    service.setChangeLogsBasePackage(scanPackage);
    List<Method> foundMethods = service.fetchChangeSets(MongockTestResource.class);

    for (Method foundMethod : foundMethods) {

      // when
      ChangeEntry entry = service.createChangeEntry(foundMethod);

      // then
      Assert.assertEquals("testuser", entry.getAuthor());
      Assert.assertEquals(MongockTestResource.class.getName(), entry.getChangeLogClass());
      Assert.assertNotNull(entry.getTimestamp());
      Assert.assertNotNull(entry.getChangeId());
      Assert.assertNotNull(entry.getChangeSetMethodName());
    }
  }

  @Test(expected = MongockException.class)
  public void shouldFailOnDuplicatedChangeSets() throws MongockException {
    String scanPackage = ChangeLogWithDuplicate.class.getPackage().getName();
    ChangeService service = new ChangeService();
    service.setChangeLogsBasePackage(scanPackage);
    service.fetchChangeSets(ChangeLogWithDuplicate.class);
  }

}
