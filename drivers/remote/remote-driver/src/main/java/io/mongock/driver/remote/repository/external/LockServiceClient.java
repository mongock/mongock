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
import io.mongock.driver.core.lock.LockEntry;

public interface LockServiceClient {

  @Headers("Content-Type: application/json")
  @RequestLine("PUT {organization}/{service}/lock/")
  void acquireLock(@Param("organization") String organization, @Param("service") String service, LockReqDto lock);

  @Headers("Content-Type: application/json")
  @RequestLine("GET {organization}/{service}/lock/{relativeKey}")
  LockEntry getByOrganizationServiceAndKey(@Param("organization") String organization,
                                           @Param("service") String service,
                                           @Param("relativeKey") String relativeKey);

  @Headers("Content-Type: application/json")
  @RequestLine("DELETE {organization}/{service}/lock/{relativeKey}")
  void removeByOrganizationAndServiceAndKey(@Param("organization") String organization,
                                            @Param("service") String service,
                                            @Param("relativeKey") String relativeKey);

  @Headers("Content-Type: application/json")
  @RequestLine("DELETE {organization}/{service}/lock/{relativeKey}")
  void removeByOrganizationAndService(@Param("organization") String organization,
                                      @Param("service") String service);


  static LockServiceClient getClient(String host) {
    return Feign.builder()
        .client(new OkHttpClient())
        .encoder(new JacksonEncoder())
        .decoder(new JacksonDecoder())
        .logger(new Slf4jLogger(LockServiceClient.class))
        .logLevel(Logger.Level.FULL)
        .target(LockServiceClient.class, host);
  }

}
