package com.ravejoy.github.api.users;

import static org.assertj.core.api.Assertions.assertThat;

import com.ravejoy.github.annotations.ApiSmoke;
import com.ravejoy.github.api.users.model.User;
import com.ravejoy.github.config.AppConfig;
import com.ravejoy.github.http.RequestSpecs;
import com.ravejoy.github.support.TestConfig;
import com.ravejoy.github.support.http.GitHubWeb;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.*;

@ApiSmoke
@Feature("Users endpoint")
class UsersSmokeTest {

  @Test
  @DisplayName("GET /users/{username} returns valid user")
  void getUserReturnsValidResponse() {
    var spec = RequestSpecs.github(AppConfig.API_URL, AppConfig.TOKEN);
    var client = new UsersClient(spec);

    String login = TestConfig.TEST_USER;
    User user = client.getUser(login);

    assertThat(user.login()).isEqualTo(login);
    assertThat(user.htmlUrl()).startsWith(GitHubWeb.BASE + "/");
  }
}
