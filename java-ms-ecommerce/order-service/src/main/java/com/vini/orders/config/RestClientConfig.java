package com.vini.orders.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

  @Bean
  RestClient.Builder restClientBuilder() {
    var cm = new PoolingHttpClientConnectionManager();
    cm.setMaxTotal(50);
    cm.setDefaultMaxPerRoute(20);

    RequestConfig requestConfig = RequestConfig.custom()
        .setConnectTimeout(Timeout.ofSeconds(2))
        .setResponseTimeout(Timeout.ofSeconds(2))
        .build();

    CloseableHttpClient httpClient = HttpClients.custom()
        .setConnectionManager(cm)
        .setDefaultRequestConfig(requestConfig)
        .evictExpiredConnections()
        .build();

    var requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

    return RestClient.builder()
        .requestFactory(requestFactory)
        .requestInterceptor((request, body, execution) -> {
          String rid = MDC.get("requestId");
          if (rid != null && !rid.isBlank()) {
            request.getHeaders().set("X-Request-Id", rid);
          }
          return execution.execute(request, body);
        });
  }
}
