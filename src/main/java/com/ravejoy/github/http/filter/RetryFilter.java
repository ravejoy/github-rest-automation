package com.ravejoy.github.http.filter;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RetryFilter implements Filter {
  private static final Logger log = LoggerFactory.getLogger(RetryFilter.class);
  private static final Set<Integer> RETRYABLE = Set.of(429, 502, 503, 504);

  private final int maxAttempts;
  private final long baseDelayMillis;
  private final long maxDelayMillis;

  public RetryFilter(int maxAttempts, long baseDelayMillis, long maxDelayMillis) {
    this.maxAttempts = Math.max(1, maxAttempts);
    this.baseDelayMillis = Math.max(1, baseDelayMillis);
    this.maxDelayMillis = Math.max(this.baseDelayMillis, maxDelayMillis);
  }

  @Override
  public Response filter(
      FilterableRequestSpecification requestSpec,
      FilterableResponseSpecification responseSpec,
      FilterContext ctx) {
    String method = requestSpec.getMethod();
    boolean eligible = "GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method);

    int attempt = 1;
    Response response = ctx.next(requestSpec, responseSpec);

    while (eligible && shouldRetry(response) && attempt < maxAttempts) {
      long delay = backoffWithJitterMillis(attempt);
      if (log.isWarnEnabled()) {
        log.warn(
            "Retryable status {} on {} {}. Attempt {}/{}. Sleeping {} ms",
            response.getStatusCode(),
            method,
            requestSpec.getURI(),
            attempt + 1,
            maxAttempts,
            delay);
      }
      sleep(delay);
      attempt++;
      response = ctx.next(requestSpec, responseSpec);
    }
    return response;
  }

  private boolean shouldRetry(Response r) {
    return RETRYABLE.contains(r.getStatusCode());
  }

  private long backoffWithJitterMillis(int attempt) {
    long exp = baseDelayMillis * (1L << Math.max(0, attempt - 1));
    long capped = Math.min(exp, maxDelayMillis);
    long jitter = ThreadLocalRandom.current().nextLong(baseDelayMillis + 1);
    long sum = capped + jitter;
    return Math.min(sum, maxDelayMillis);
  }

  private void sleep(long ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
  }
}
