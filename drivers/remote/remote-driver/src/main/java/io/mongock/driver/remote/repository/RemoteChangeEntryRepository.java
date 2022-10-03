package io.mongock.driver.remote.repository;

import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.driver.api.entry.ChangeEntryService;
import io.mongock.driver.remote.repository.external.ChangeEntryDto;
import io.mongock.driver.remote.repository.external.ChangeEntryServiceClient;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class RemoteChangeEntryRepository implements ChangeEntryService {

  private final ChangeEntryServiceClient client;
  private final String organization;
  private final String service;

  public RemoteChangeEntryRepository(ChangeEntryServiceClient client, String organization, String service) {
    this.client = client;
    this.organization = organization;
    this.service = service;
  }

  @Override
  public void initialize() {

  }

  @Override
  public void saveOrUpdate(ChangeEntry changeEntry) throws MongockException {
    client.putChange(organization, service, buildDto(changeEntry));
  }

  @Override
  public List<ChangeEntry> getEntriesLog() {
    return client.getByOrganizationAndService(organization, service);
  }

  @Override
  public void deleteAll() {
    //doing nothing, it's for testing
  }

  @Override
  public void ensureField(Field field) {
    //do nothing because is already handled in the server
  }

  @Override
  public void setIndexCreation(boolean indexCreation) {
    //do nothing because is already handled in the server
  }

  private ChangeEntryDto buildDto(ChangeEntry changeEntry) {
    return new ChangeEntryDto(
        changeEntry.getExecutionId(),
        changeEntry.getChangeId(),
        changeEntry.getAuthor(),
        LocalDateTime.ofInstant(changeEntry.getTimestamp().toInstant(), ZoneId.systemDefault()),
        changeEntry.getState(),
        changeEntry.getType(),
        changeEntry.getChangeLogClass(),
        changeEntry.getChangeSetMethod(),
        changeEntry.getExecutionMillis(),
        changeEntry.getExecutionHostname(),
        changeEntry.getMetadata()
//        ,changeEntry.getErrorTrace().orElse(null)
    );
  }
}
