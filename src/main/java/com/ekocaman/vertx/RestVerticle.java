package com.ekocaman.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import org.jboss.resteasy.plugins.server.vertx.VertxRequestHandler;
import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;

public class RestVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(RestVerticle.class);

    @Override
    public void start() throws Exception {
        final VertxResteasyDeployment deployment = new VertxResteasyDeployment();
        deployment.start();
        deployment.getRegistry().addPerInstanceResource(TodoController.class);

        final VertxRequestHandler vertxRequestHandler = new VertxRequestHandler(vertx, deployment);

        Router router = Router.router(vertx);
        router.route("/api/*").handler(routingContext -> {
            vertxRequestHandler.handle(routingContext.request());
        });
        router.route().handler(StaticHandler.create());

        HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(router::accept);
        httpServer.listen(8080);

        logger.info(this.getClass().getName() + " is deployed successfully");
    }
}
