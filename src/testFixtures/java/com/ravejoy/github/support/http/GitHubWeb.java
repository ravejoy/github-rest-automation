package com.ravejoy.github.support.http;

public final class GitHubWeb {
  private GitHubWeb() {}
  public static final String BASE = "https://github.com";

  public static String userHtml(String login) {
    return BASE + "/" + login;
  }
}
