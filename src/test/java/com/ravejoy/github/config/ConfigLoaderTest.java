package com.ravejoy.github.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.*;

@Tag("infra")
@Tag("unit")
@Epic("Test Infrastructure")
@Feature("Configuration loader")
class ConfigLoaderTest {

  private static final String REQ = "X_REQUIRED";
  private static final String OPT = "X_OPTIONAL";
  private static final String SYS_VAL = "sys-value";
  private static final String DEF_VAL = "def-val";

  @BeforeEach
  void clear() {
    System.clearProperty(REQ);
    System.clearProperty(OPT);
  }

  @AfterEach
  void cleanup() {
    System.clearProperty(REQ);
    System.clearProperty(OPT);
  }

  @Test
  @DisplayName("required(key) reads from system properties")
  @Severity(SeverityLevel.CRITICAL)
  void requiredReadsFromSystemProperty() {
    System.setProperty(REQ, SYS_VAL);
    String v = ConfigLoader.required(REQ);
    assertThat(v).isEqualTo(SYS_VAL);
  }

  @Test
  @DisplayName("required(key) throws when missing")
  @Severity(SeverityLevel.BLOCKER)
  void requiredThrowsWhenMissing() {
    assertThatThrownBy(() -> ConfigLoader.required(REQ))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Missing required config");
  }

  @Test
  @DisplayName("optional(key, def) returns default when missing")
  @Severity(SeverityLevel.MINOR)
  void optionalReturnsDefault() {
    String v = ConfigLoader.optional(OPT, DEF_VAL);
    assertThat(v).isEqualTo(DEF_VAL);
  }
}
