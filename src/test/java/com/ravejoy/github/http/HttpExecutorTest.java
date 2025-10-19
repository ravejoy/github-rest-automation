package com.ravejoy.github.http;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;

/** Integration-style test for HttpExecutor retry behavior using MockWebServer. */
class HttpExecutorTest {

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
  void getRetriesOnceOn503AndSucceedsOnSecondAttempt() {
    server.enqueue(new MockResponse().setResponseCode(503).setBody("{}"));
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"ok\":true}"));

    var resp = HttpExecutor.getWithRetry(spec, "rate_limit", 3, null);

    assertThat(resp.statusCode()).isEqualTo(200);
    assertThat(server.getRequestCount()).isEqualTo(2);
  }

  @Test
  @DisplayName("POST is not retried on 503")
  void postIsNotRetriedOn503() {
    server.enqueue(new MockResponse().setResponseCode(503));

    var resp = HttpExecutor.postWithRetry(spec, "repos", "{}", 3, null);

    assertThat(resp.statusCode()).isEqualTo(503);
    assertThat(server.getRequestCount()).isEqualTo(1);
  }

  @Test
  @DisplayName("GET 429 with Retry-After is not retried")
  void get429WithRetryAfterIsNotRetried() {
    server.enqueue(new MockResponse().setResponseCode(429).addHeader("Retry-After", "5"));

    var resp = HttpExecutor.getWithRetry(spec, "search", 4, null);

    assertThat(resp.statusCode()).isEqualTo(429);
    assertThat(server.getRequestCount()).isEqualTo(1);
  }

  @Test
  @DisplayName("GET fails after max attempts on repeated 503")
  void getFailsAfterMaxAttempts() {
    server.enqueue(new MockResponse().setResponseCode(503));
    server.enqueue(new MockResponse().setResponseCode(503));
    server.enqueue(new MockResponse().setResponseCode(503));

    var resp = HttpExecutor.getWithRetry(spec, "ping", 3, null);

    assertThat(resp.statusCode()).isEqualTo(503);
    assertThat(server.getRequestCount()).isEqualTo(3);
  }
}
