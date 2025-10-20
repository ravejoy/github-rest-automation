package com.ravejoy.github.config;

public final class AppConfig {
  private static final ConfigLoader L = new ConfigLoader("application.properties");

  public static final String API_URL = L.required("api.url");
  public static final String TOKEN = L.required("token");

  private AppConfig() {}
}
