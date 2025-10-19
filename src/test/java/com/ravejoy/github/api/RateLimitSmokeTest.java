package com.ravejoy.github.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.ravejoy.github.config.AppConfig;
import com.ravejoy.github.http.RequestSpecs;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

class RateLimitSmokeTest {

  @Test
  void rateLimitEndpointResponds200AndHasResourcesNode() {
    var json =
        RestAssured.given()
            .spec(RequestSpecs.github(AppConfig.API_URL, AppConfig.TOKEN))
            .when()
            .get("/rate_limit")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath();

    assertThat(json.getMap("resources")).isNotNull();
    assertThat(json.getMap("resources.core")).isNotNull();
  }
}
