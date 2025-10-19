package com.ravejoy.github.config;

public final class AppConfig {
  public static final String API_URL = valueOrDefault("GITHUB_API_URL", "https://api.github.com");
  public static final String TOKEN = valueOrDefault("GITHUB_TOKEN", "");

  private AppConfig() {}

  private static String valueOrDefault(String key, String def) {
    String v = System.getenv(key);
    if (v == null || v.isBlank()) {
      v = System.getProperty(key, def);
    }
    return v;
  }
}
