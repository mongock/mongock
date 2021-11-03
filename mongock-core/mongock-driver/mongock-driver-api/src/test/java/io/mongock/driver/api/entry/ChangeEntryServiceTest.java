package io.mongock.driver.api.entry;

import io.mongock.api.exception.MongockException;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static io.mongock.driver.api.entry.ChangeState.EXECUTED;
import static io.mongock.driver.api.entry.ChangeState.IGNORED;
import static org.junit.Assert.*;

public class ChangeEntryServiceTest {

  @Test
  public void getAllEntriesWithCurrentState() {

    Instant now = Instant.now();
    List<ChangeEntry> entries =  Arrays.asList(
      getChangeEntry("change-1", now, EXECUTED),
        getChangeEntry("change-1", now, EXECUTED)
    );

  }


  static ChangeEntry getChangeEntry(String changeId, Instant instant, ChangeState state) {
    return new ChangeEntry(
        "executionId",
        changeId,
        "author",
        Date.from(instant),
        EXECUTED,
        ChangeType.EXECUTION,
        "changeLogClass",
        "changeSetMethod",
        1000L,
        "executionHostname",
        new Object());
  }
  static class ChangeEntryServiceImpl implements ChangeEntryService<ChangeEntry> {

    private final List<ChangeEntry> entries;

    public ChangeEntryServiceImpl(List<ChangeEntry> entries) {
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
    public void save(ChangeEntry changeEntry) throws MongockException {

    }

    @Override
    public void initialize() {

    }
  }
}
