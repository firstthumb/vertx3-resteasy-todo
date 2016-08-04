package com.ekocaman.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/tasks")
public class TodoController {
    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);

    @GET
    @Path("/")
    @Produces("application/json")
    public void get(@Suspended final AsyncResponse asyncResponse, @Context Vertx vertx) {
        vertx.eventBus().<JsonObject>send(TodoBackend.CONSUMER_ID, new JsonObject().put(TodoBackend.CONSUMER_OPERATION, TodoBackend.OPERATION_LIST), msg -> {
            if (msg.succeeded()) {
                JsonObject json = msg.result().body();
                if (json != null) {
                    logger.info("RESPONSE : " + json.encode());
                    asyncResponse.resume(json.encode());
                } else {
                    asyncResponse.resume(Response.status(Response.Status.NOT_FOUND).build());
                }
            } else {
                asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
            }
        });
    }

    @GET
    @Path("/{taskId}")
    @Produces("application/json")
    public void get(@Suspended final AsyncResponse asyncResponse, @Context Vertx vertx, @PathParam("taskId") String taskId) {
        vertx.eventBus().<JsonObject>send(TodoBackend.CONSUMER_ID,
                new JsonObject()
                        .put(TodoBackend.CONSUMER_OPERATION, TodoBackend.OPERATION_GET)
                        .put(TodoBackend.PARAM_TASK_ID, taskId),
                msg -> {
                    if (msg.succeeded()) {
                        JsonObject json = msg.result().body();
                        if (json != null) {
                            logger.info("RESPONSE : " + json.encode());
                            asyncResponse.resume(json.encode());
                        } else {
                            asyncResponse.resume(Response.status(Response.Status.NOT_FOUND).build());
                        }
                    } else {
                        asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
                    }
                });
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void save(@Suspended final AsyncResponse asyncResponse, @Context Vertx vertx, Task task) {
        vertx.eventBus().<JsonObject>send(TodoBackend.CONSUMER_ID,
                new JsonObject()
                        .put(TodoBackend.CONSUMER_OPERATION, TodoBackend.OPERATION_ADD)
                        .put(TodoBackend.PARAM_TASK_OBJECT, Json.encode(task)),
                msg -> {
                    if (msg.succeeded()) {
                        JsonObject json = msg.result().body();
                        if (json != null) {
                            logger.info("RESPONSE : " + json.encode());
                            asyncResponse.resume(json.encode());
                        } else {
                            asyncResponse.resume(Response.status(Response.Status.NOT_FOUND).build());
                        }
                    } else {
                        asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
                    }
                });
    }

    @PUT
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void update(@Suspended final AsyncResponse asyncResponse, @Context Vertx vertx, Task task) {
        vertx.eventBus().<JsonObject>send(TodoBackend.CONSUMER_ID,
                new JsonObject()
                        .put(TodoBackend.CONSUMER_OPERATION, TodoBackend.OPERATION_UPDATE)
                        .put(TodoBackend.PARAM_TASK_OBJECT, Json.encode(task)),
                msg -> {
                    if (msg.succeeded()) {
                        JsonObject json = msg.result().body();
                        if (json != null) {
                            logger.info("RESPONSE : " + json.encode());
                            asyncResponse.resume(json.encode());
                        } else {
                            asyncResponse.resume(Response.status(Response.Status.NOT_FOUND).build());
                        }
                    } else {
                        asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
                    }
                });
    }
}
