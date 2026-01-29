package com.vini.orders;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.moreThanOrExactly;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OrderServiceIT {

  static WireMockServer wiremock;

  @DynamicPropertySource
  static void props(DynamicPropertyRegistry r) {
    // base-urls vao para o WireMock (Inventory + Payment simulados)
    r.add("clients.inventory.base-url", () -> "http://localhost:" + wiremock.port());
    r.add("clients.payment.base-url", () -> "http://localhost:" + wiremock.port());
  }

  @BeforeAll
  static void beforeAll() {
    wiremock = new WireMockServer(0);
    wiremock.start();
    configureFor("localhost", wiremock.port());
  }

  @AfterAll
  static void afterAll() {
    if (wiremock != null) wiremock.stop();
  }

  @BeforeEach
  void reset() {
    wiremock.resetAll();
  }

  @org.springframework.beans.factory.annotation.Autowired
  TestRestTemplate http;

  @Test
  void should_create_order_and_mark_as_paid_when_payment_ok() {
    // Inventory reserve OK
    stubFor(post(urlEqualTo("/inventory/reserve"))
        .willReturn(aResponse().withStatus(200)));

    // Payment OK
    stubFor(post(urlEqualTo("/payments"))
        .willReturn(aResponse().withStatus(200)));

    var body = Map.of(
        "customerId", "c1",
        "items", new Object[] {
            Map.of("sku", "SKU-1", "quantity", 2),
            Map.of("sku", "SKU-2", "quantity", 1)
        }
    );

    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("X-Request-Id", "it-001");

    ResponseEntity<Map> res = http.exchange(
        "/orders",
        HttpMethod.POST,
        new HttpEntity<>(body, headers),
        Map.class
    );

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(res.getBody()).isNotNull();
    assertThat(res.getBody().get("status")).isEqualTo("PAID");

    // garante que chamou reserve e payment 1x
    wiremock.verify(1, postRequestedFor(urlEqualTo("/inventory/reserve")));
    wiremock.verify(1, postRequestedFor(urlEqualTo("/payments")));
    // nao liberou estoque
    wiremock.verify(0, postRequestedFor(urlEqualTo("/inventory/release")));
  }

  @Test
  void should_cancel_and_release_when_payment_declined() {
    // reserve OK
    stubFor(post(urlEqualTo("/inventory/reserve"))
        .willReturn(aResponse().withStatus(200)));

    // payment DECLINED (retorna erro)
    stubFor(post(urlEqualTo("/payments"))
        .willReturn(aResponse().withStatus(402)));

    // release OK
    stubFor(post(urlEqualTo("/inventory/release"))
        .willReturn(aResponse().withStatus(200)));

    var body = Map.of(
        "customerId", "c1",
        "items", new Object[] {
            Map.of("sku", "SKU-1", "quantity", 1)
        }
    );

    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("X-Request-Id", "it-002");

    ResponseEntity<String> res = http.exchange(
        "/orders",
        HttpMethod.POST,
        new HttpEntity<>(body, headers),
        String.class
    );

    if (res.getStatusCode().is5xxServerError()) {
      System.out.println("STATUS=" + res.getStatusCode());
      System.out.println("BODY=" + res.getBody());
    }

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(res.getBody()).isNotNull();

    Map bodyMap;
    try {
      bodyMap = new ObjectMapper().readValue(res.getBody(), Map.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse response body", e);
    }

    assertThat(bodyMap.get("status")).isEqualTo("CANCELED");

    wiremock.verify(1, postRequestedFor(urlEqualTo("/inventory/reserve")));
    // pode chamar payment mais de 1x se retry estiver ligado; entao validamos >= 1
    wiremock.verify(moreThanOrExactly(1), postRequestedFor(urlEqualTo("/payments")));
    // compensacao obrigatoria
    wiremock.verify(1, postRequestedFor(urlEqualTo("/inventory/release")));
  }

  @Test
  void getOrder_whenNotFound_shouldReturn404() {
    var id = UUID.randomUUID();

    ResponseEntity<Map> res = http.exchange(
        "/orders/" + id,
        HttpMethod.GET,
        new HttpEntity<>(null, new HttpHeaders()),
        Map.class
    );

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(res.getBody()).isNotNull();
    assertThat(res.getBody().get("title")).isEqualTo("Not Found");
    assertThat(res.getBody().get("detail").toString()).contains("Order not found");
  }
}
