package com.ravejoy.github.api.users;

import static org.assertj.core.api.Assertions.*;

import com.ravejoy.github.annotations.InfraUnit;
import com.ravejoy.github.http.RequestSpecs;
import com.ravejoy.github.http.StatusCode;
import com.ravejoy.github.support.fixtures.UserResponse;
import com.ravejoy.github.support.http.GitHubPaths;
import com.ravejoy.github.support.http.GitHubWeb;
import com.ravejoy.github.support.http.MockJson;
import io.qameta.allure.Feature;
import io.restassured.specification.RequestSpecification;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;

@InfraUnit
@Feature("Users client")
class UsersClientTest {

  private static final String USER_LOGIN = "ravejoy-test";
  private static final long USER_ID = 42L;

  private MockWebServer server;
  private RequestSpecification spec;
  private UsersClient client;

  @BeforeEach
  void setUp() throws Exception {
    server = new MockWebServer();
    server.start();
    spec = RequestSpecs.mock(server.url("").toString());
    client = new UsersClient(spec);
  }

  @AfterEach
  void tearDown() throws Exception {
    server.shutdown();
  }

  @Test
  @DisplayName("getUser() deserializes basic user JSON correctly")
  void getUserDeserializesCorrectly() throws Exception {
    server.enqueue(MockJson.ok(UserResponse.of(USER_ID, USER_LOGIN)));
    var user = client.getUser(USER_LOGIN);
    assertThat(user.id()).isEqualTo(USER_ID);
    assertThat(user.login()).isEqualTo(USER_LOGIN);
    assertThat(user.htmlUrl()).isEqualTo(GitHubWeb.userHtml(USER_LOGIN));

    var recorded = server.takeRequest();
    assertThat(recorded.getPath()).isEqualTo(GitHubPaths.user(USER_LOGIN));
  }

  @Test
  @DisplayName("503 x3 → retries exactly 3 times, then fails")
  void getUserFailsAfterThree503Retries() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(StatusCode.SERVICE_UNAVAILABLE));
    server.enqueue(new MockResponse().setResponseCode(StatusCode.SERVICE_UNAVAILABLE));
    server.enqueue(new MockResponse().setResponseCode(StatusCode.SERVICE_UNAVAILABLE));

    assertThatThrownBy(() -> client.getUser(USER_LOGIN))
        .isInstanceOf(AssertionError.class)
        .hasMessageContaining("503");

    assertThat(server.getRequestCount()).isEqualTo(3);
  }

  @Test
  @DisplayName("503, 503, 200 → succeeds on last retry")
  void getUserRecoversAfterTwo503Retries() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(StatusCode.SERVICE_UNAVAILABLE));
    server.enqueue(new MockResponse().setResponseCode(StatusCode.SERVICE_UNAVAILABLE));
    server.enqueue(MockJson.ok(UserResponse.of(USER_ID, USER_LOGIN)));

    var user = client.getUser(USER_LOGIN);

    assertThat(user.login()).isEqualTo(USER_LOGIN);
    assertThat(server.getRequestCount()).isEqualTo(3);
  }

  @Test
  @DisplayName("404 Not Found → no retries, immediate failure")
  void getUserFailsImmediatelyOn404() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(StatusCode.NOT_FOUND));

    assertThatThrownBy(() -> client.getUser(USER_LOGIN))
        .isInstanceOf(AssertionError.class)
        .hasMessageContaining("404");

    assertThat(server.getRequestCount()).isEqualTo(1);
  }
}
