package com.ravejoy.github.support.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UncheckedIOException;
import okhttp3.mockwebserver.MockResponse;

public final class MockJson {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private MockJson() {}

  public static MockResponse ok(Object body) {
    try {
      return new MockResponse()
          .setResponseCode(200)
          .addHeader("Content-Type", "application/json")
          .setBody(MAPPER.writeValueAsString(body));
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }
}
