package com.ekocaman.vertx;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TodoBackend extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(TodoBackend.class);

    public static final String GLOBAL_EVENT_BUS_SERVER = "app.to.server";
    public static final String GLOBAL_EVENT_BUS_CLIENT = "app.to.client";

    public static final String CONSUMER_ID = "backend";
    public static final String CONSUMER_OPERATION = "operation";

    public static final String OPERATION_LIST = "list";
    public static final String OPERATION_ADD = "add";
    public static final String OPERATION_UPDATE = "update";
    public static final String OPERATION_DELETE = "delete";
    public static final String OPERATION_GET = "get";

    public static final String PARAM_TASK_ID = "taskId";
    public static final String PARAM_TASK_OBJECT = "task";

    private static final Map<String, Task> TASKS = new HashMap<>();

    public TodoBackend() {
        // Create initial data
        for (int i = 0; i < 5; i++) {
            String taskId = UUID.randomUUID().toString();
            TASKS.put(taskId,
                    Task.builder()
                            .id(taskId)
                            .message("task" + i)
                            .completed(false)
                            .created(new Date())
                            .build()
            );
        }

        logger.info("Added sample tasks");
    }

    @Override
    public void start() throws Exception {
        vertx.eventBus().<JsonObject>consumer(CONSUMER_ID, msg -> {
            JsonObject json = msg.body();

            logger.info("Running backend job : " + json);
            switch (json.getString(CONSUMER_OPERATION, "")) {
                case OPERATION_GET: {
                    String taskId = json.getString(PARAM_TASK_ID);
                    msg.reply(new JsonObject(Json.encode(TASKS.get(taskId))));
                    break;
                }

                case OPERATION_ADD: {
                    String jsonString = json.getString(PARAM_TASK_OBJECT);
                    Task task = Json.decodeValue(jsonString, Task.class);
                    if (task.getId() == null || task.getId().isEmpty()) {
                        task.setId(UUID.randomUUID().toString());
                        task.setCreated(new Date());
                    }
                    TASKS.put(task.getId(), task);
                    msg.reply(new JsonObject(Json.encode(new TaskList(TASKS.values()))));
                    notifyForChanges();
                    break;
                }

                case OPERATION_UPDATE: {
                    String taskId = json.getString(PARAM_TASK_ID);
                    String jsonString = json.getString(PARAM_TASK_OBJECT);
                    Task task = Json.decodeValue(jsonString, Task.class);
                    if (taskId == null || taskId.isEmpty()) {
                        msg.fail(0, "Task does not exist");
                    } else {
                        task.setId(taskId);
                        TASKS.put(taskId, task);
                        msg.reply(new JsonObject(Json.encode(new TaskList(TASKS.values()))));
                        notifyForChanges();
                    }
                    break;
                }

                case OPERATION_DELETE: {
                    String taskId = json.getString(PARAM_TASK_ID);
                    TASKS.remove(taskId);
                    msg.reply(new JsonObject(Json.encode(new TaskList(TASKS.values()))));
                    notifyForChanges();
                    break;
                }

                case OPERATION_LIST: {
                    msg.reply(new JsonObject(Json.encode(new TaskList(TASKS.values()))));
                    break;
                }

                default:
                    logger.warn("Operation not permitted");
                    msg.fail(0, "Operation not permitted");
            }
        });

        // If client sends message, return tasks list
        vertx.eventBus().consumer(GLOBAL_EVENT_BUS_SERVER).handler(message -> {
            vertx.eventBus().publish(GLOBAL_EVENT_BUS_CLIENT, new JsonObject(Json.encode(new TaskList(TASKS.values()))));
        });

        logger.info(this.getClass().getName() + " is deployed successfully");
    }

    private void notifyForChanges() {
        vertx.eventBus().publish(GLOBAL_EVENT_BUS_CLIENT, new JsonObject(Json.encode(new TaskList(TASKS.values()))));
    }
}
