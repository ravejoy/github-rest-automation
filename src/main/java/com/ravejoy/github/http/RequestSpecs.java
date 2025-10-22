package com.ravejoy.github.http;

import com.ravejoy.github.http.filter.HttpLoggingFilter;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;

public final class RequestSpecs {

  private RequestSpecs() {}

  public static RequestSpecification github(String baseUrl, String token) {
    RequestSpecBuilder b = commonSpec(trimTrailingSlash(baseUrl));
    if (token != null && !token.isBlank()) {
      b.addHeader("Authorization", "Bearer " + token);
    }
    return b.build();
  }

  public static RequestSpecification mock(String baseUrl) {
    return commonSpec(trimTrailingSlash(baseUrl)).build();
  }

  private static RequestSpecBuilder commonSpec(String baseUrl) {
    return new RequestSpecBuilder()
        .setBaseUri(baseUrl)
        .addHeader("Accept", "application/vnd.github+json")
        .addHeader("X-GitHub-Api-Version", "2022-11-28")
        .log(LogDetail.URI)
        .addFilter(new HttpLoggingFilter());
  }

  private static String trimTrailingSlash(String s) {
    if (s == null || s.isEmpty()) return s;
    return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
  }
}
