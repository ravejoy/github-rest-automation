package com.ravejoy.github.api.users.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record User(
    long id,
    String login,
    @JsonProperty("html_url") String htmlUrl,
    String name,
    String company,
    String location,
    String bio) {}
