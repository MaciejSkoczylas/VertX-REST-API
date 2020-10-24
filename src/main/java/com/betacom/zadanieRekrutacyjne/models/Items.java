package com.betacom.zadanieRekrutacyjne.models;

import io.vertx.core.json.JsonObject;

import java.util.UUID;

public class Items {

  private UUID id;
  private UUID owner;
  private String name;

  public JsonObject itemToJson() {
    JsonObject itemJson = new JsonObject()
      .put("id", this.id)
      .put("owner", this.owner)
      .put("name", name);

    return itemJson;
  }


  public Items(UUID id, UUID owner, String name) {
    this.id = id;
    this.owner = owner;
    this.name = name;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getOwner() {
    return owner;
  }

  public void setOwner(UUID owner) {
    this.owner = owner;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
