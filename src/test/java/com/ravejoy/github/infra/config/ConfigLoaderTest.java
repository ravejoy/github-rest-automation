package com.ravejoy.github.infra.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ravejoy.github.annotations.InfraUnit;
import com.ravejoy.github.config.ConfigLoader;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.*;

@InfraUnit
@Feature("Configuration loader")
class ConfigLoaderTest {

  private static final String REQ = "X_REQUIRED";
  private static final String OPT = "X_OPTIONAL";
  private static final String SYS_VAL = "sys-value";
  private static final String DEF_VAL = "def-val";

  private ConfigLoader loader;

  @BeforeEach
  void setUp() {
    loader = new ConfigLoader(null);
    System.clearProperty(REQ);
    System.clearProperty(OPT);
  }

  @AfterEach
  void tearDown() {
    System.clearProperty(REQ);
    System.clearProperty(OPT);
  }

  @Test
  @DisplayName("required(key) reads from system properties")
  @Severity(SeverityLevel.CRITICAL)
  void requiredReadsFromSystemProperty() {
    System.setProperty(REQ, SYS_VAL);
    String v = loader.required(REQ);
    assertThat(v).isEqualTo(SYS_VAL);
  }

  @Test
  @DisplayName("required(key) throws when missing")
  @Severity(SeverityLevel.BLOCKER)
  void requiredThrowsWhenMissing() {
    assertThatThrownBy(() -> loader.required(REQ))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Required config key is missing");
  }

  @Test
  @DisplayName("optional(key, def) returns default when missing")
  @Severity(SeverityLevel.MINOR)
  void optionalReturnsDefault() {
    String v = loader.optional(OPT, DEF_VAL);
    assertThat(v).isEqualTo(DEF_VAL);
  }
}
