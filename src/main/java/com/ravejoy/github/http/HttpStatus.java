package com.ravejoy.github.http;

/**
 * Common HTTP status codes for better readability in assertions and filters.
 */
public enum HttpStatus {
    OK(200),
    CREATED(201),
    NO_CONTENT(204),
    NOT_FOUND(404),
    TOO_MANY_REQUESTS(429),
    INTERNAL_SERVER_ERROR(500),
    BAD_GATEWAY(502),
    SERVICE_UNAVAILABLE(503),
    GATEWAY_TIMEOUT(504);

    private final int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public int value() {
        return code;
    }
}
