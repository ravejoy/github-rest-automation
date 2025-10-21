package com.ravejoy.github.config;

import java.io.InputStream;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;

public final class ConfigLoader {
  private final Properties props;

  public ConfigLoader(String resourceName) {
    this.props = new Properties();
    if (resourceName != null && !resourceName.isBlank()) {
      try (InputStream is =
          Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
        if (is != null) {
          this.props.load(is);
        }
      } catch (Exception e) {
        throw new IllegalStateException("Failed to load properties: " + resourceName, e);
      }
    }
  }

  public String required(String key) {
    return lookup(key)
        .orElseThrow(() -> new IllegalStateException("Required config key is missing: " + key));
  }

  public String optional(String key, String def) {
    return lookup(key).orElse(def);
  }

  private Optional<String> lookup(String key) {
    String fromSys = System.getProperty(key);
    if (present(fromSys)) return Optional.of(fromSys);

    String fromEnvExact = System.getenv(key);
    if (present(fromEnvExact)) return Optional.of(fromEnvExact);

    String envKey = key.replace('.', '_').replace('-', '_').toUpperCase(Locale.ROOT);
    String fromEnvUpper = System.getenv(envKey);
    if (present(fromEnvUpper)) return Optional.of(fromEnvUpper);

    String fromProps = props.getProperty(key);
    if (present(fromProps)) return Optional.of(fromProps);

    return Optional.empty();
  }

  private static boolean present(String v) {
    return v != null && !v.isBlank();
  }
}
