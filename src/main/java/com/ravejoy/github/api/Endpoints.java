package com.ravejoy.github.api;

public final class Endpoints {
  private Endpoints() {}

  public static final class Github {
    private Github() {}

    public static final String RATE_LIMIT = "rate_limit";
    public static final String REPOS = "repos";
    public static final String SEARCH = "search";
  }

  public static final class Mock {
    private Mock() {}

    public static final String PING = "ping";
  }
}
