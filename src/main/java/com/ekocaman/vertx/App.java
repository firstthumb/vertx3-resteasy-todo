package com.ekocaman.vertx;

import io.vertx.core.VertxOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.Vertx;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        Vertx.clusteredVertxObservable(new VertxOptions().setClustered(true)).subscribe(vertx -> {
                    logger.info("Deploying verticle...");
                    vertx.deployVerticle(RestVerticle.class.getName());
                    vertx.deployVerticle(TodoBackend.class.getName());
                },
                error -> {
                    logger.error("Could not deploy verticle, reason : {}", error);
                }
        );
    }
}
