package com.ravejoy.github.http.filter;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public final class HttpLoggingFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(HttpLoggingFilter.class);

    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        if (log.isDebugEnabled()) {
            String uri = requestSpec.getURI();
            String method = requestSpec.getMethod();
            log.debug("{} {}", method, uri);

            for (Map.Entry<String, String> h : requestSpec.getHeaders().asList()) {
                String name = h.getKey();
                String value = h.getValue();
                if ("Authorization".equalsIgnoreCase(name)) {
                    value = "****";
                }
                log.debug("H {}: {}", name, value);
            }
        }

        Response response = ctx.next(requestSpec, responseSpec);

        if (log.isDebugEnabled()) {
            log.debug("Status {}", response.getStatusCode());
        }
        return response;
    }
}
