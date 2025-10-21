package com.ravejoy.github.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("infra")
@Tag("unit")
@Epic("Test Infrastructure")
@Feature("Application config")
class AppConfigTest {

  @Test
  @DisplayName("AppConfig exposes non-blank API_URL and TOKEN")
  @Severity(SeverityLevel.CRITICAL)
  void exposesNonBlankConfig() {
    assertThat(AppConfig.API_URL).isNotBlank().startsWith("http");
    assertThat(AppConfig.TOKEN).isNotBlank();
  }
}
