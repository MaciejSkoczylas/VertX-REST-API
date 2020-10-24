package com.betacom.zadanieRekrutacyjne;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.ext.auth.mongo.HashAlgorithm;
import io.vertx.ext.auth.mongo.MongoAuth;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;


public class MainVerticle extends AbstractVerticle {

  public static final String USERS = "user";
  public static final String ITEMS = "item";

  private MongoClient mongoClient;
  private MongoAuth auth;

  private JWTAuth jwtAuth;


  @Override
  public void start(Promise<Void> startPromise) {

    dbconfig();
    authConfig();

    Router router = Router.router(vertx);

    router.get("/items").handler(JWTAuthHandler.create(jwtAuth)).handler(this::getitemsHandler);
    router.post().handler(BodyHandler.create());
    router.post("/login").handler(this::loginHandler);
    router.post("/register").handler(this::registerHandler);
    router.post("/items").handler(JWTAuthHandler.create(jwtAuth)).handler(this::postItemsHandler);

    vertx.createHttpServer().requestHandler(router)
      .listen(
        config().getInteger("http.port", 3000),
        result -> {
          if (result.succeeded()) {
            startPromise.complete();
          } else {
            startPromise.fail(result.cause());
          }
        }
      );
  }

  private void postItemsHandler(RoutingContext context) {

    String userId = context.user().principal().getString("id");

    JsonObject item = new JsonObject()
      .put("name", context.getBodyAsJson().getString("name"))
      .put("owner", userId);

    if (item.getString("name").length() >= 5) {
      mongoClient.save(ITEMS, item, res -> {
        if (res.succeeded()) {
          context.request().response()
            .setStatusCode(204)
            .end(res.result());
        } else {
          context.request().response()
            .setStatusCode(401)
            .end();
        }
      });
    } else {
      context.request().response()
        .setStatusCode(400)
        .end("Name wymaga przynajmniej 5 znaków");
    }
  }

  private void registerHandler(RoutingContext context) {

    JsonObject user = context.getBodyAsJson();

    if (user.getString("login").length() >= 5 && user.getString("password").length() >= 5) {
      auth.insertUser(user.getString("login"), user.getString("password"), null, null, res -> {
        if (res.succeeded()) {
          context.response()
            .setStatusCode(204)
            .putHeader("Content-Type", "application/json; charset=utf-8")
            .end(Json.encodePrettily(user));
        } else {
          context.response()
            .setStatusCode(500)
            .end(Json.encodePrettily(res.cause().getMessage()));
        }
      });
    } else {
      context.response()
        .setStatusCode(400)
        .end("Login i hasło wymagają przynajmniej 5 znaków");
    }
  }

  private void loginHandler(RoutingContext context) {

    JsonObject user = context.getBodyAsJson();

    auth.authenticate(user, res -> {
      if (res.succeeded()) {
        String id = res.result().principal().getString("_id");
        String token = jwtAuth.generateToken(new JsonObject().put("id", id), new JWTOptions().setIgnoreExpiration(true));
        context.response()
          .setStatusCode(200)
          .putHeader("Content-Type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(token));
      } else {
        context.response()
          .setStatusCode(401)
          .end();
      }
    });
  }

  private void getitemsHandler(RoutingContext context) {

    String userId = context.user().principal().getString("id");

    JsonObject query = new JsonObject()
      .put("owner", userId);

    mongoClient.find(ITEMS, query, res -> {
      if (res.succeeded()) {
        context.response()
          .setStatusCode(200)
          .end(Json.encodePrettily(res.result()));

      } else {
        context.response()
          .setStatusCode(401)
          .end();
      }
    });
  }

  private void authConfig() {
    jwtAuth = JWTAuth.create(vertx, new JWTAuthOptions()
      .setKeyStore(new KeyStoreOptions()
        .setPath("keystore.jceks")
        .setType("jceks")
        .setPassword("secret")));

    auth = MongoAuth.create(mongoClient, new JsonObject())
      .setHashAlgorithm(HashAlgorithm.PBKDF2)
      .setUsernameField("login")
      .setUsernameCredentialField("login")
      .setCollectionName(USERS);
  }

  private void dbconfig() {
    mongoClient = MongoClient.createShared(vertx, new JsonObject().put("db_name", config().getString("db_name", "baziwo")));

    mongoClient.createCollection(USERS, res -> {
    });

    mongoClient.createCollection(ITEMS, res -> {
    });
  }
}










