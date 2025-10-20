package com.ravejoy.github.http;

import static com.ravejoy.github.http.StatusCode.OK;
import static com.ravejoy.github.http.StatusCode.SERVICE_UNAVAILABLE;
import static com.ravejoy.github.http.StatusCode.TOO_MANY_REQUESTS;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("infra")
@Tag("unit")
@Epic("HTTP infra")
@Feature("Retry logic")
class HttpExecutorTest {

  private static final String RATE_LIMIT = "rate_limit";
  private static final String REPOS = "repos";
  private static final String SEARCH = "search";
  private static final String PING = "ping";

  private final ObjectMapper om = new ObjectMapper();

  private MockWebServer server;
  private RequestSpecification spec;

  @BeforeEach
  void setUp() throws Exception {
    server = new MockWebServer();
    server.start();
    String base = server.url("").toString();
    spec =
        new RequestSpecBuilder()
            .setBaseUri(base.endsWith("/") ? base.substring(0, base.length() - 1) : base)
            .addHeader("Accept", "application/vnd.github+json")
            .addHeader("X-GitHub-Api-Version", "2022-11-28")
            .build();
  }

  @AfterEach
  void tearDown() throws Exception {
    server.shutdown();
  }

  @Test
  @DisplayName("GET retries once on 503 and succeeds on second attempt")
  void getRetriesOnceOn503AndSucceedsOnSecondAttempt() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE).setBody(om.writeValueAsString(java.util.Map.of())));
    server.enqueue(new MockResponse().setResponseCode(OK).setBody(om.writeValueAsString(java.util.Map.of("ok", true))));

    var resp = HttpExecutor.getWithRetry(spec, RATE_LIMIT, 3, null);

    assertThat(resp.statusCode()).isEqualTo(OK);
    assertThat(server.getRequestCount()).isEqualTo(2);
  }

  @Test
  @DisplayName("POST is not retried on 503")
  void postIsNotRetriedOn503() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE));

    var resp = HttpExecutor.postWithRetry(spec, REPOS, om.writeValueAsString(java.util.Map.of()), 3, null);

    assertThat(resp.statusCode()).isEqualTo(SERVICE_UNAVAILABLE);
    assertThat(server.getRequestCount()).isEqualTo(1);
  }

  @Test
  @DisplayName("GET 429 with Retry-After is not retried")
  void get429WithRetryAfterIsNotRetried() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(TOO_MANY_REQUESTS).addHeader("Retry-After", "5"));

    var resp = HttpExecutor.getWithRetry(spec, SEARCH, 4, null);

    assertThat(resp.statusCode()).isEqualTo(TOO_MANY_REQUESTS);
    assertThat(server.getRequestCount()).isEqualTo(1);
  }

  @Test
  @DisplayName("GET fails after max attempts on repeated 503")
  void getFailsAfterMaxAttempts() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE));
    server.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE));
    server.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE));

    var resp = HttpExecutor.getWithRetry(spec, PING, 3, null);

    assertThat(resp.statusCode()).isEqualTo(SERVICE_UNAVAILABLE);
    assertThat(server.getRequestCount()).isEqualTo(3);
  }
}
