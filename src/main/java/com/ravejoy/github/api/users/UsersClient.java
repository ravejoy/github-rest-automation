package com.ravejoy.github.api.users;

import static com.ravejoy.github.http.StatusCode.OK;

import com.ravejoy.github.api.Endpoints;
import com.ravejoy.github.api.users.model.User;
import com.ravejoy.github.http.HttpExecutor;
import io.restassured.specification.RequestSpecification;

public final class UsersClient {

  private final RequestSpecification spec;

  public UsersClient(RequestSpecification spec) {
    this.spec = spec;
  }

  public User getUser(String username) {
    var resp = HttpExecutor.getWithRetry(spec, Endpoints.Github.user(username), 3, null);
    resp.then().statusCode(OK);
    return resp.as(User.class);
  }
}
