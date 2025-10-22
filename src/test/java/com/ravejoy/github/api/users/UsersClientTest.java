package com.ravejoy.github.api.users;

import static com.ravejoy.github.http.StatusCode.OK;
import static org.assertj.core.api.Assertions.assertThat;

import com.ravejoy.github.annotations.InfraUnit;
import com.ravejoy.github.http.RequestSpecs;
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
    var json =
        """
        {
          "id": %d,
          "login": "%s",
          "html_url": "https://github.com/%s",
          "name": "Test User",
          "company": "Test ltd",
          "location": "Earth",
          "bio": "Some interesting bio"
        }
        """
            .formatted(USER_ID, USER_LOGIN, USER_LOGIN);

    server.enqueue(
        new MockResponse()
            .setResponseCode(OK)
            .addHeader("Content-Type", "application/json")
            .setBody(json));

    var user = client.getUser(USER_LOGIN);

    assertThat(user.id()).isEqualTo(USER_ID);
    assertThat(user.login()).isEqualTo(USER_LOGIN);
    assertThat(user.htmlUrl()).contains("github.com");
    assertThat(user.name()).isEqualTo("Test User");

    var recorded = server.takeRequest();
    assertThat(recorded.getPath()).isEqualTo("/users/" + USER_LOGIN);
  }
}
