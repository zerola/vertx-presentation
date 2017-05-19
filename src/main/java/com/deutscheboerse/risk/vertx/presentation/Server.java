package com.deutscheboerse.risk.vertx.presentation;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class Server extends AbstractVerticle {

    @Override
    public void start(Future<Void> future) {
        Router router = Router.router(vertx);
        router.route("/*").handler(StaticHandler.create("presentation"));
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8080, result -> {
                    if (result.succeeded()) {
                        future.complete();
                    } else {
                        future.fail(result.cause());
                    }
                });
    }
}
