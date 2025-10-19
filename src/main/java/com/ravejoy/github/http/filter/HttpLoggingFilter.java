package com.ravejoy.github.http.filter;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HttpLoggingFilter implements Filter {
  private static final Logger log = LoggerFactory.getLogger(HttpLoggingFilter.class);

  @Override
  public Response filter(
      FilterableRequestSpecification requestSpec,
      FilterableResponseSpecification responseSpec,
      FilterContext ctx) {
    if (log.isDebugEnabled()) {
      log.debug("{} {}", requestSpec.getMethod(), requestSpec.getURI());
      for (Header h : requestSpec.getHeaders()) {
        String v = "Authorization".equalsIgnoreCase(h.getName()) ? "****" : h.getValue();
        log.debug("H {}: {}", h.getName(), v);
      }
    }

    Response response = ctx.next(requestSpec, responseSpec);

    if (log.isDebugEnabled()) {
      log.debug("Status {}", response.getStatusCode());
    }
    return response;
  }
}
