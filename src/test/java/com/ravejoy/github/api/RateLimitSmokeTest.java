package com.ravejoy.github.api;

import static com.ravejoy.github.http.StatusCode.OK;
import static org.assertj.core.api.Assertions.assertThat;

import com.ravejoy.github.annotations.ApiSmoke;
import com.ravejoy.github.config.AppConfig;
import com.ravejoy.github.http.RequestSpecs;
import com.ravejoy.github.support.TestConfig;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@ApiSmoke
@Feature("Rate limit endpoint")
class RateLimitSmokeTest {

  @Test
  @DisplayName("GET /rate_limit returns 200 and has 'resources' node")
  void rateLimitEndpointResponds200AndHasResourcesNode() {
    var spec = RequestSpecs.github(AppConfig.API_URL, AppConfig.TOKEN);
    var resp = RestAssured.given().spec(spec).get(Endpoints.Github.RATE_LIMIT);

    resp.then().statusCode(OK);
    assertThat(resp.jsonPath().getMap("resources")).isNotNull();
    assertThat(TestConfig.TEST_USER).isNotBlank();
  }
}
