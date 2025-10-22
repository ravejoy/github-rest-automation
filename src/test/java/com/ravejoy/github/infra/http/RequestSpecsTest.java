package com.ravejoy.github.infra.http;

import static com.ravejoy.github.http.StatusCode.OK;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.specification.RequestSpecification;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.ravejoy.github.http.RequestSpecs;

@Tag("infra")
@Tag("unit")
@Epic("HTTP infra")
@Feature("RequestSpecs")
class RequestSpecsTest {

  private MockWebServer server;

  @BeforeEach
  void setUp() throws Exception {
    server = new MockWebServer();
    server.start();
  }

  @AfterEach
  void tearDown() throws Exception {
    server.shutdown();
  }

  @Test
  @DisplayName("github(base, token) adds Authorization header when token present")
  void githubAddsAuthHeaderWhenTokenPresent() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(OK).setBody("{}"));
    String baseWithSlash = server.url("").toString(); // deliberately ends with '/'

    RequestSpecification spec = RequestSpecs.github(baseWithSlash, "TOK");
    var resp = given().spec(spec).get("ping").then().extract();

    assertThat(resp.statusCode()).isEqualTo(OK);

    var req = server.takeRequest();
    assertThat(req.getHeader("Authorization")).isEqualTo("Bearer TOK");
    assertThat(req.getHeader("Accept")).isEqualTo("application/vnd.github+json");
    assertThat(req.getHeader("X-GitHub-Api-Version")).isEqualTo("2022-11-28");
    assertThat(req.getPath()).isEqualTo("/ping");
  }

  @Test
  @DisplayName("github(base, null) does not add Authorization header")
  void githubDoesNotAddAuthHeaderWhenTokenNull() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(OK).setBody("{}"));
    String base = server.url("").toString();

    RequestSpecification spec = RequestSpecs.github(base, null);
    given().spec(spec).get("rate_limit").then().statusCode(OK);

    var req = server.takeRequest();
    assertThat(req.getHeader("Authorization")).isNull();
  }

  @Test
  @DisplayName("github(base, \" \") does not add Authorization header (blank token branch)")
  void githubDoesNotAddAuthHeaderWhenTokenBlank() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(OK).setBody("{}"));
    String base = server.url("").toString();

    RequestSpecification spec = RequestSpecs.github(base, "   ");
    given().spec(spec).get("search").then().statusCode(OK);

    var req = server.takeRequest();
    assertThat(req.getHeader("Authorization")).isNull();
  }

  @Test
  @DisplayName("mock(base) sets common headers and trims trailing slash")
  void mockSetsCommonHeadersAndTrimsSlash() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(OK).setBody("{}"));
    String baseWithSlash = server.url("").toString(); // ends with '/'

    RequestSpecification spec = RequestSpecs.mock(baseWithSlash);
    given().spec(spec).get("ping").then().statusCode(OK);

    var req = server.takeRequest();
    assertThat(req.getHeader("Authorization")).isNull();
    assertThat(req.getHeader("Accept")).isEqualTo("application/vnd.github+json");
    assertThat(req.getHeader("X-GitHub-Api-Version")).isEqualTo("2022-11-28");
    assertThat(req.getPath()).isEqualTo("/ping");
  }
}
