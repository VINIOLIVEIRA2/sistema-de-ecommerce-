package com.vini.gateway;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class RequestIdGatewayFilter implements GlobalFilter, Ordered {

  private static final String HEADER = "X-Request-Id";

  @Override
  public Mono<Void> filter(org.springframework.cloud.gateway.filter.GatewayFilterChain chain,
                          org.springframework.web.server.ServerWebExchange exchange) {

    String requestId = exchange.getRequest().getHeaders().getFirst(HEADER);
    if (requestId == null || requestId.isBlank()) requestId = UUID.randomUUID().toString();

    ServerHttpRequest mutated = exchange.getRequest().mutate()
        .header(HEADER, requestId)
        .build();

    exchange.getResponse().getHeaders().set(HEADER, requestId);

    return chain.filter(exchange.mutate().request(mutated).build())
        .doOnSubscribe(sub -> MDC.put("requestId", requestId))
        .doFinally(sig -> MDC.remove("requestId"));
  }

  @Override
  public int getOrder() {
    return -1;
  }
}
