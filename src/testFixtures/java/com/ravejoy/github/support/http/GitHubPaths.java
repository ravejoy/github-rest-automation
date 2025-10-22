package com.ravejoy.github.support.http;

public final class GitHubPaths {
  private GitHubPaths() {}
  public static String user(String login) { return "/users/" + login; }
}
