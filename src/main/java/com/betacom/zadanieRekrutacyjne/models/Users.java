package com.betacom.zadanieRekrutacyjne.models;

import io.vertx.core.json.JsonObject;

import java.util.UUID;

public class Users {

  private UUID id;
  private String login;
  private String password;

  public JsonObject userToJson() {
    JsonObject jsonUser = new JsonObject()
      .put("id", this.id)
      .put("login", this.login)
      .put("password", this.password);

    return jsonUser;
  }

  public Users(UUID id, String login, String password) {
    this.id = id;
    this.login = login;
    this.password = password;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
