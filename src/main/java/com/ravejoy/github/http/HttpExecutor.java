package com.ravejoy.github.http;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Set;

public final class HttpExecutor {
  private static final Set<Integer> DEFAULT_RETRYABLE = Set.of(429, 502, 503, 504);

  private HttpExecutor() {}

  public static Response getWithRetry(
      RequestSpecification spec, String path, int maxAttempts, Set<Integer> retryableStatuses) {
    int attempts = Math.max(1, maxAttempts);
    Set<Integer> retryable =
        retryableStatuses == null || retryableStatuses.isEmpty()
            ? DEFAULT_RETRYABLE
            : retryableStatuses;

    Response last = null;
    for (int i = 1; i <= attempts; i++) {
      last = RestAssured.given().spec(spec).when().get(path);
      int sc = last.getStatusCode();

      if (i < attempts && shouldRetry("GET", sc, retryable, last)) {
        continue;
      }
      return last;
    }
    return last;
  }

  public static Response postWithRetry(
      RequestSpecification spec,
      String path,
      Object body,
      int maxAttempts,
      Set<Integer> retryableStatuses) {
    return RestAssured.given().spec(spec).body(body).when().post(path);
  }

  private static boolean shouldRetry(
      String method, int status, Set<Integer> retryable, Response resp) {
    if (!("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method))) return false;
    if (!retryable.contains(status)) return false;
    if (status == 429) {
      String ra = resp.getHeader("Retry-After");
      if (ra != null && !ra.isBlank()) return false;
    }
    return true;
  }
}
