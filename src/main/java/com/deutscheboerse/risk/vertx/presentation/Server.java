package com.deutscheboerse.risk.vertx.presentation;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

public class Server extends AbstractVerticle {

    @Override
    public void start(Future<Void> future) {
        EventBus eb = vertx.eventBus();
        final Collection<AtomicLong> timersID = new ArrayList<>();
        final AtomicLong messageID = new AtomicLong();
        eb.consumer("start", message -> {
            final AtomicLong newTimer = new AtomicLong();
            newTimer.set(vertx.setPeriodic(1000, id -> {
                String text = String.format("New message %s from Vert.x (timer: %d)", messageID.incrementAndGet(), id);
                eb.publish("bcast", text);
            }));
            timersID.add(newTimer);
        });
        eb.consumer("stop", message -> {
            timersID.forEach(id -> vertx.cancelTimer(id.get()));
            timersID.clear();
            messageID.set(0);
        });

        Router router = Router.router(vertx);

        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        BridgeOptions options = new BridgeOptions()
            .addInboundPermitted(new PermittedOptions().setAddress("start"))
            .addInboundPermitted(new PermittedOptions().setAddress("stop"))
            .addOutboundPermitted(new PermittedOptions().setAddress("bcast"));
        sockJSHandler.bridge(options);

        router.route("/ebus.html").handler(ctx -> ctx.response()
                .putHeader("X-Frame-Options", "ALLOWALL")
                .sendFile("presentation/ebus.html"));
        router.route("/eventbus/*").handler(sockJSHandler);
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
