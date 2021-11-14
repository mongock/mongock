package io.mongock.driver.api.entry;

import io.mongock.api.exception.MongockException;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static io.mongock.driver.api.entry.ChangeState.EXECUTED;
import static io.mongock.driver.api.entry.ChangeState.FAILED;
import static io.mongock.driver.api.entry.ChangeState.IGNORED;
import static io.mongock.driver.api.entry.ChangeState.ROLLBACK_FAILED;
import static io.mongock.driver.api.entry.ChangeState.ROLLED_BACK;
import static org.junit.Assert.*;

public class ChangeEntryServiceTest {

  @Test
  public void getAllEntriesWithCurrentState() {

    ChangeEntryServiceImpl entryService = getChangeEntryService();
    List<ChangeEntry> result = entryService.getAllEntriesWithCurrentState();
    result.sort(Comparator.comparing(ChangeEntry::getChangeId));

    int i = 0;
    assertEquals("change-" + (i+1), result.get(i).getChangeId());
    assertEquals(EXECUTED, result.get(i).getState());

    i++;
    assertEquals("change-10", result.get(i).getChangeId());
    assertEquals(ROLLBACK_FAILED, result.get(i).getState());

    i++;
    assertEquals("change-" + i, result.get(i).getChangeId());
    assertEquals(EXECUTED, result.get(i).getState());

    i++;
    assertEquals("change-" + i, result.get(i).getChangeId());
    assertEquals(EXECUTED, result.get(i).getState());

    i++;
    assertEquals("change-" + i, result.get(i).getChangeId());
    assertEquals(EXECUTED, result.get(i).getState());

    i++;
    assertEquals("change-" + i, result.get(i).getChangeId());
    assertEquals(FAILED, result.get(i).getState());

    i++;
    assertEquals("change-" + i, result.get(i).getChangeId());
    assertEquals(FAILED, result.get(i).getState());

    i++;
    assertEquals("change-" + i, result.get(i).getChangeId());
    assertEquals(ROLLED_BACK, result.get(i).getState());

    i++;
    assertEquals("change-" + i, result.get(i).getChangeId());
    assertEquals(ROLLED_BACK, result.get(i).getState());

    i++;
    assertEquals("change-" + i, result.get(i).getChangeId());
    assertEquals(ROLLBACK_FAILED, result.get(i).getState());
  }

  @Test
  public void getExecuted() {

    ChangeEntryServiceImpl entryService = getChangeEntryService();
    List<ExecutedChangeEntry> result = entryService.getExecuted();
    result.sort(Comparator.comparing(ExecutedChangeEntry::getChangeId));

    assertEquals("change-1", result.get(0).getChangeId());
    assertEquals("change-2", result.get(1).getChangeId());
    assertEquals("change-3", result.get(2).getChangeId());
    assertEquals("change-4", result.get(3).getChangeId());
    assertEquals("change-5", result.get(4).getChangeId());
    assertEquals("change-6", result.get(5).getChangeId());
    assertEquals("change-9", result.get(6).getChangeId());
  }

  private ChangeEntryServiceImpl getChangeEntryService() {
    Instant now = Instant.now();
    List<ChangeEntry> changeEntries = new ArrayList<>();
    changeEntries.addAll(getEntryNullStateOverIgnored(1, (now = now.plusMillis(1000))));
    changeEntries.addAll(getEntryNullState(2, (now = now.plusMillis(1000))));
    changeEntries.addAll(getEntryExecutedStateOverIgnored(3, (now = now.plusMillis(1000))));
    changeEntries.addAll(getEntryExecutedState(4, (now = now.plusMillis(1000))));
    changeEntries.addAll(getEntryFailedStateOverIgnored(5, (now = now.plusMillis(1000))));
    changeEntries.addAll(getEntryFailedState(6, (now = now.plusMillis(1000))));
    changeEntries.addAll(getEntryRollbackStateOverIgnored(7, (now = now.plusMillis(1000))));
    changeEntries.addAll(getEntryRollbackState(8, (now = now.plusMillis(1000))));
    changeEntries.addAll(getEntryRollbackFailedStateOverIgnored(9, (now = now.plusMillis(1000))));
    changeEntries.addAll(getEntryRollbackFailedState(10, (now = now.plusMillis(1000))));
    ChangeEntryServiceImpl entryService = new ChangeEntryServiceImpl(changeEntries);
    return entryService;
  }

  private static List<ChangeEntry> getEntryNullStateOverIgnored(int id, Instant instant) {
    
    return Arrays.asList(
        getChangeEntry("change-" + id, instant, IGNORED),
        getChangeEntry("change-" + id, instant.minusMillis(1000), null),
        getChangeEntry("change-" + id, instant.minusMillis(2000), FAILED),
        getChangeEntry("change-" + id, instant.minusMillis(3000), ROLLED_BACK)
    );
  }

  private static List<ChangeEntry> getEntryNullState(int id, Instant instant) {
    
    return Arrays.asList(
        getChangeEntry("change-" + id, instant.minusMillis(1000), null),
        getChangeEntry("change-" + id, instant.minusMillis(2000), FAILED),
        getChangeEntry("change-" + id, instant.minusMillis(3000), ROLLED_BACK)
    );
  }


  private static List<ChangeEntry> getEntryExecutedStateOverIgnored(int id, Instant instant) {
    
    return Arrays.asList(
        getChangeEntry("change-" + id, instant, IGNORED),
        getChangeEntry("change-" + id, instant.minusMillis(1000), EXECUTED),
        getChangeEntry("change-" + id, instant.minusMillis(2000), FAILED),
        getChangeEntry("change-" + id, instant.minusMillis(3000), ROLLED_BACK)
    );
  }


  private static List<ChangeEntry> getEntryExecutedState(int id, Instant instant) {
    
    return Arrays.asList(
        getChangeEntry("change-" + id, instant.minusMillis(1000), EXECUTED),
        getChangeEntry("change-" + id, instant.minusMillis(2000), FAILED),
        getChangeEntry("change-" + id, instant.minusMillis(3000), ROLLED_BACK)
    );
  }

  private static List<ChangeEntry> getEntryFailedStateOverIgnored(int id, Instant instant) {
    
    return Arrays.asList(
        getChangeEntry("change-" + id, instant, IGNORED),
        getChangeEntry("change-" + id, instant.minusMillis(1000), FAILED),
        getChangeEntry("change-" + id, instant.minusMillis(2000), EXECUTED),
        getChangeEntry("change-" + id, instant.minusMillis(3000), ROLLED_BACK)
    );
  }


  private static List<ChangeEntry> getEntryFailedState(int id, Instant instant) {
    
    return Arrays.asList(
        getChangeEntry("change-" + id, instant.minusMillis(1000), FAILED),
        getChangeEntry("change-" + id, instant.minusMillis(2000), EXECUTED),
        getChangeEntry("change-" + id, instant.minusMillis(3000), ROLLED_BACK)
    );
  }

  private static List<ChangeEntry> getEntryRollbackStateOverIgnored(int id, Instant instant) {
    
    return Arrays.asList(
        getChangeEntry("change-" + id, instant, IGNORED),
        getChangeEntry("change-" + id, instant.minusMillis(1000), ROLLED_BACK),
        getChangeEntry("change-" + id, instant.minusMillis(2000), EXECUTED),
        getChangeEntry("change-" + id, instant.minusMillis(3000), ROLLED_BACK)
    );
  }


  private static List<ChangeEntry> getEntryRollbackState(int id, Instant instant) {
    
    return Arrays.asList(
        getChangeEntry("change-" + id, instant.minusMillis(1000), ROLLED_BACK),
        getChangeEntry("change-" + id, instant.minusMillis(2000), EXECUTED),
        getChangeEntry("change-" + id, instant.minusMillis(3000), ROLLED_BACK)
    );
  }


  private static List<ChangeEntry> getEntryRollbackFailedStateOverIgnored(int id, Instant instant) {
    
    return Arrays.asList(
        getChangeEntry("change-" + id, instant, IGNORED),
        getChangeEntry("change-" + id, instant.minusMillis(1000), ROLLBACK_FAILED),
        getChangeEntry("change-" + id, instant.minusMillis(2000), EXECUTED),
        getChangeEntry("change-" + id, instant.minusMillis(3000), ROLLED_BACK)
    );
  }


  private static List<ChangeEntry> getEntryRollbackFailedState(int id, Instant instant) {
    
    return Arrays.asList(
        getChangeEntry("change-" + id, instant.minusMillis(1000), ROLLBACK_FAILED),
        getChangeEntry("change-" + id, instant.minusMillis(2000), FAILED),
        getChangeEntry("change-" + id, instant.minusMillis(3000), ROLLED_BACK)
    );
  }


  static ChangeEntry getChangeEntry(String changeId, Instant instant, ChangeState state) {
    return new ChangeEntry(
        "executionId",
        changeId,
        "author",
        Date.from(instant),
        state,
        ChangeType.EXECUTION,
        "changeLogClass",
        "changeSetMethod",
        1000L,
        "executionHostname",
        new Object());
  }

  private static class ChangeEntryServiceImpl implements ChangeEntryService {

    private final List<ChangeEntry> entries;

    private ChangeEntryServiceImpl(List<ChangeEntry> entries) {
      this.entries = entries;
    }

    @Override
    public List<ChangeEntry> getEntriesLog() {
      return entries;
    }

    @Override
    public void setIndexCreation(boolean indexCreation) {

    }

    @Override
    public boolean isAlreadyExecuted(String changeSetId, String author) throws MongockException {
      return false;
    }

    @Override
    public void saveOrUpdate(ChangeEntry changeEntry) throws MongockException {

    }


    @Override
    public void initialize() {

    }
  }
}
