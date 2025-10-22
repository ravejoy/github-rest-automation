package com.ravejoy.github.support.fixtures;

public record UserResponse(
    long id,
    String login,
    String html_url,
    String name,
    String company,
    String location,
    String bio
) {
  public static UserResponse of(long id, String login) {
    return new UserResponse(
        id,
        login,
        "https://github.com/" + login,
        "Test User",
        "Test ltd",
        "Earth",
        "Some interesting bio"
    );
  }
}
