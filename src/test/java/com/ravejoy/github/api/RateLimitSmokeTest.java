package com.ravejoy.github;

import static com.ravejoy.github.http.StatusCode.OK;
import static org.assertj.core.api.Assertions.assertThat;

import com.ravejoy.github.config.AppConfig;
import com.ravejoy.github.config.TestConfig;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("api")
@Tag("smoke")
@Epic("GitHub API")
@Feature("Rate limit endpoint")
class RateLimitSmokeTest {

  private static final String RATE_LIMIT = "/rate_limit";

  @Test
  @DisplayName("GET /rate_limit returns 200 and has 'resources' node")
  void rateLimitEndpointResponds200AndHasResourcesNode() {
    var resp = RestAssured.given().baseUri(AppConfig.API_URL).get(RATE_LIMIT);

    resp.then().statusCode(OK);
    assertThat(resp.jsonPath().getMap("resources")).isNotNull();
    assertThat(TestConfig.TEST_USER).isNotBlank();
  }
}
