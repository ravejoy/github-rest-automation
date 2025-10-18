package com.ravejoy.github.http;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;

public final class RequestSpecs {
  private RequestSpecs() {}

  public static RequestSpecification github(String baseUrl, String token) {
    RequestSpecBuilder builder =
        new RequestSpecBuilder()
            .setBaseUri(baseUrl)
            .addHeader("Accept", "application/vnd.github+json")
            .addHeader("X-GitHub-Api-Version", "2022-11-28")
            .log(LogDetail.URI);

    if (token != null && !token.isBlank()) {
      builder.addHeader("Authorization", "Bearer " + token);
    }
    return builder.build();
  }
}
