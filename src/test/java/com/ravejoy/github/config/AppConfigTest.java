package com.ravejoy.github.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.*;

@Tag("infra")
@Tag("unit")
@Epic("Test Infrastructure")
@Feature("Application config")
class AppConfigTest {

  private static final String KEY_API = "API_URL";
  private static final String KEY_TOKEN = "TOKEN";
  private static final String API_VAL = "https://api.github.com";
  private static final String TOKEN_VAL = "ci-token";

  @BeforeAll
  static void setProps() {
    System.setProperty(KEY_API, API_VAL);
    System.setProperty(KEY_TOKEN, TOKEN_VAL);
  }

  @AfterAll
  static void clearProps() {
    System.clearProperty(KEY_API);
    System.clearProperty(KEY_TOKEN);
  }

  @Test
  @DisplayName("AppConfig reads required keys from system properties")
  @Severity(SeverityLevel.CRITICAL)
  void readsFromSystemProperties() {
    assertThat(AppConfig.API_URL).isEqualTo(API_VAL);
    assertThat(AppConfig.TOKEN).isEqualTo(TOKEN_VAL);
  }
}
