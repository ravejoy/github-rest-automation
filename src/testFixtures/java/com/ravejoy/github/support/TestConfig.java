package com.ravejoy.github.support;

import com.ravejoy.github.config.ConfigLoader;

public final class TestConfig {
  private static final ConfigLoader L = new ConfigLoader("test.properties");

  public static final String TEST_USER = L.required("test.user");

  private TestConfig() {}
}
