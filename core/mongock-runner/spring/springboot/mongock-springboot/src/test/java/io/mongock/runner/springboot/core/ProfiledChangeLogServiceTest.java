package io.mongock.runner.springboot.core;


import io.mongock.api.ChangeLogItem;
import io.mongock.api.ChangeSetItem;
import io.mongock.runner.core.executor.changelog.ChangeLogService;
import io.mongock.runner.spring.base.util.ProfileUtil;
import io.mongock.runner.springboot.profiles.defaultprofiled.DefaultProfiledChangerLog;
import io.mongock.runner.springboot.profiles.dev.DevProfiledChangerLog;
import io.mongock.runner.springboot.profiles.pro.ProProfiledChangeLog;
import io.mongock.runner.springboot.profiles.unprofiled.UnprofiledChangerLog;
import org.junit.Test;
import org.springframework.context.annotation.Profile;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ProfiledChangeLogServiceTest {
  private static final Function<List<String>, Function<AnnotatedElement, Boolean>> profileFilter =
      activeProfiles -> annotated -> ProfileUtil.matchesActiveSpringProfile(
          activeProfiles,
          Profile.class,
          annotated,
          (AnnotatedElement element) -> element.getAnnotation(Profile.class).value());


  @Test
  public void shouldRunDevProfileAndNonAnnotated() throws NoSuchMethodException {
    ChangeLogService changeLogService = new ChangeLogService(
        Collections.singletonList(DevProfiledChangerLog.class.getPackage().getName()),
        Collections.emptyList(),
        "0",
        String.valueOf(Integer.MAX_VALUE),
        profileFilter.apply(Collections.singletonList("dev"))
    );

    ChangeLogItem<ChangeSetItem> changeLog = new ArrayList<>(changeLogService.fetchChangeLogs()).get(0);
    assertEquals(DevProfiledChangerLog.class, changeLog.getType());
    assertEquals(DevProfiledChangerLog.class, changeLog.getType());
    assertEquals("01", changeLog.getOrder());
    assertEquals(2, changeLog.getChangeSetItems().size());

    ChangeSetItem changeSet = changeLog.getChangeSetItems().get(0);
    assertEquals("Pdev1", changeSet.getId());
    assertEquals("testuser", changeSet.getAuthor());
    assertFalse(changeSet.isRunAlways());
    assertEquals(DevProfiledChangerLog.class.getMethod("testChangeSet"), changeSet.getMethod());
    assertNull(changeSet.getMethod().getAnnotation(Profile.class));


    changeSet = changeLog.getChangeSetItems().get(1);
    assertEquals("Pdev4", changeSet.getId());
    assertEquals("testuser", changeSet.getAuthor());
    assertTrue(changeSet.isRunAlways());
    assertEquals(DevProfiledChangerLog.class.getMethod("testChangeSet4"), changeSet.getMethod());
    List<String> profiles = Arrays.asList(changeSet.getMethod().getAnnotation(Profile.class).value());
    assertEquals(1, profiles.size());
    assertTrue(profiles.contains("dev"));
  }

  @Test
  public void shouldRunUnProfiledChangeLog_ifMethodsProfiled_WhenDefaultProfile() throws NoSuchMethodException {
    ChangeLogService changeLogService = new ChangeLogService(
        Collections.singletonList(ProProfiledChangeLog.class.getPackage().getName()),
        Collections.emptyList(),
        "0",
        String.valueOf(Integer.MAX_VALUE),
        profileFilter.apply(Collections.singletonList("default"))
    );

    ChangeLogItem<ChangeSetItem> changeLog = new ArrayList<>(changeLogService.fetchChangeLogs()).get(0);
    assertEquals(ProProfiledChangeLog.class, changeLog.getType());
    assertEquals(ProProfiledChangeLog.class, changeLog.getType());
    assertEquals(2, changeLog.getChangeSetItems().size());

    ChangeSetItem changeSet = changeLog.getChangeSetItems().get(0);
    assertEquals("no-profiled", changeSet.getId());
    assertEquals("testuser", changeSet.getAuthor());
    assertFalse(changeSet.isRunAlways());
    assertEquals(ProProfiledChangeLog.class.getMethod("noProfiledMethod"), changeSet.getMethod());
    assertNull(changeSet.getMethod().getAnnotation(Profile.class));


    changeSet = changeLog.getChangeSetItems().get(1);
    assertEquals("no-pro-profiled", changeSet.getId());
    assertEquals("testuser", changeSet.getAuthor());
    assertTrue(changeSet.isRunAlways());
    assertEquals(ProProfiledChangeLog.class.getMethod("noProProfiledMethod"), changeSet.getMethod());
    List<String> profiles = Arrays.asList(changeSet.getMethod().getAnnotation(Profile.class).value());
    assertEquals(1, profiles.size());
    assertTrue(profiles.contains("!pro"));
  }

  @Test
  public void shouldNotRunAnyChangeSet_whenAnotherProfile() {
    ChangeLogService changeLogService = new ChangeLogService(
        Collections.singletonList(DevProfiledChangerLog.class.getPackage().getName()),
        Collections.emptyList(),
        "0",
        String.valueOf(Integer.MAX_VALUE),
        profileFilter.apply(Collections.singletonList("anotherProfile"))
    );
    assertEquals(0, changeLogService.fetchChangeLogs().size());
  }


  @Test
  public void shouldRunAllChangeSets_WhenNoProfileInvolved() throws NoSuchMethodException {

    ChangeLogService changeLogService = new ChangeLogService(
        Collections.singletonList(UnprofiledChangerLog.class.getPackage().getName()),
        Collections.emptyList(),
        "0",
        String.valueOf(Integer.MAX_VALUE),
        profileFilter.apply(Collections.singletonList("anotherProfile"))
    );

    ChangeLogItem<ChangeSetItem> changeLog = new ArrayList<>(changeLogService.fetchChangeLogs()).get(0);
    assertEquals(UnprofiledChangerLog.class, changeLog.getType());
    assertEquals(UnprofiledChangerLog.class, changeLog.getType());
    assertEquals(1, changeLog.getChangeSetItems().size());

    ChangeSetItem changeSet = changeLog.getChangeSetItems().get(0);
    assertEquals("no-profiled", changeSet.getId());
    assertEquals("testuser", changeSet.getAuthor());
    assertFalse(changeSet.isRunAlways());
    assertEquals(UnprofiledChangerLog.class.getMethod("noProfiled"), changeSet.getMethod());
    assertNull(changeSet.getMethod().getAnnotation(Profile.class));
  }


  @Test
  public void shouldRunAllChangeSet_whenDefaultProfile_IfDefaultAndEmptyProfile() throws NoSuchMethodException {
    ChangeLogService changeLogService = new ChangeLogService(
        Collections.singletonList(DefaultProfiledChangerLog.class.getPackage().getName()),
        Collections.emptyList(),
        "0",
        String.valueOf(Integer.MAX_VALUE),
        profileFilter.apply(Collections.singletonList("default"))
    );

    ChangeLogItem<ChangeSetItem> changeLog = new ArrayList<>(changeLogService.fetchChangeLogs()).get(0);
    assertEquals(DefaultProfiledChangerLog.class, changeLog.getType());
    assertEquals(DefaultProfiledChangerLog.class, changeLog.getType());
    assertEquals("01", changeLog.getOrder());
    assertEquals(2, changeLog.getChangeSetItems().size());

    ChangeSetItem changeSet = changeLog.getChangeSetItems().get(0);
    assertEquals("default-profiled", changeSet.getId());
    assertEquals("testuser", changeSet.getAuthor());
    assertFalse(changeSet.isRunAlways());
    assertEquals(DefaultProfiledChangerLog.class.getMethod("defaultProfiled"), changeSet.getMethod());
    List<String> profiles = Arrays.asList(changeSet.getMethod().getAnnotation(Profile.class).value());
    assertEquals(1, profiles.size());
    assertTrue(profiles.contains("default"));

    changeSet = changeLog.getChangeSetItems().get(1);
    assertEquals("no-profiled", changeSet.getId());
    assertEquals("testuser", changeSet.getAuthor());
    assertFalse(changeSet.isRunAlways());
    assertEquals(DefaultProfiledChangerLog.class.getMethod("noProfiled"), changeSet.getMethod());
    assertNull(changeSet.getMethod().getAnnotation(Profile.class));
  }


}
