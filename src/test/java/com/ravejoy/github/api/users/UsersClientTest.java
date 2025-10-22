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
          "id": 42,
          "login": "ravejoy-test",
          "html_url": "https://github.com/ravejoy-test",
          "name": "Test User",
          "company": "Test Ltd.",
          "location": "Earth",
          "bio": "Some interesting bio"
        }
        """;

    server.enqueue(new MockResponse().setResponseCode(OK).setBody(json));

    var user = client.getUser("ravejoy-test");

    assertThat(user.id()).isEqualTo(42);
    assertThat(user.login()).isEqualTo("ravejoy-test");
    assertThat(user.htmlUrl()).contains("github.com");
    assertThat(user.name()).isEqualTo("Test User");
  }
}
