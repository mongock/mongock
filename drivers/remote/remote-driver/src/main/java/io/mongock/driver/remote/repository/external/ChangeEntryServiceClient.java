package io.mongock.driver.remote.repository.external;

import feign.Feign;
import feign.Headers;
import feign.Logger;
import feign.Param;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import io.mongock.driver.api.entry.ChangeEntry;

import java.util.List;

public interface ChangeEntryServiceClient {

  @Headers("Content-Type: application/json")
  @RequestLine("PUT {organization}/{service}/change")
  void putChange(@Param("organization") String organization, @Param("service") String service, ChangeEntryDto changeEntry);

  @Headers("Content-Type: application/json")
  @RequestLine("GET {organization}/{service}/change")
  List<ChangeEntry> getByOrganizationAndService(@Param("organization") String organization, @Param("service") String service);


  static ChangeEntryServiceClient getClient(String host) {
    return Feign.builder()
        .client(new OkHttpClient())
        .encoder(new JacksonEncoder())
        .decoder(new JacksonDecoder())
        .logger(new Slf4jLogger(ChangeEntryServiceClient.class))
        .logLevel(Logger.Level.FULL)
        .target(ChangeEntryServiceClient.class, host);
  }

}
