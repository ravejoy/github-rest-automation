package com.ravejoy.github.http;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.ravejoy.github.http.filter.HttpLoggingFilter;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;

class HttpLoggingFilterTest {

  private MockWebServer server;
  private RequestSpecification spec;

  private Logger filterLogger;
  private ListAppender<ILoggingEvent> appender;

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
            .addFilter(new HttpLoggingFilter())
            .build();

    filterLogger = (Logger) LoggerFactory.getLogger(HttpLoggingFilter.class);
    appender = new ListAppender<>();
    appender.start();
    filterLogger.addAppender(appender);
  }

  @AfterEach
  void tearDown() throws Exception {
    if (filterLogger != null && appender != null) {
      filterLogger.detachAppender(appender);
      appender.stop();
    }
    server.shutdown();
  }

  @Test
  @DisplayName("Authorization value is masked on DEBUG")
  void authorizationIsMaskedOnDebug() {
    filterLogger.setLevel(Level.DEBUG);
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"ok\":true}"));

    var resp =
        given()
            .spec(spec)
            .header("Authorization", "Bearer TOP_SECRET_TOKEN")
            .when()
            .get("ping")
            .then()
            .extract();

    assertThat(resp.statusCode()).isEqualTo(200);

    var messages = appender.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
    assertThat(messages.stream().anyMatch(m -> m.contains("H Authorization: ****"))).isTrue();
    assertThat(messages.stream().anyMatch(m -> m.contains("TOP_SECRET_TOKEN"))).isFalse();
  }

  @Test
  @DisplayName("No DEBUG logs emitted when level is INFO")
  void noDebugLogsWhenInfoLevel() {
    filterLogger.setLevel(Level.INFO);
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

    var resp =
        given()
            .spec(spec)
            .header("Authorization", "Bearer ABC")
            .when()
            .get("rate_limit")
            .then()
            .extract();

    assertThat(resp.statusCode()).isEqualTo(200);
    assertThat(appender.list).isEmpty();
  }
}
