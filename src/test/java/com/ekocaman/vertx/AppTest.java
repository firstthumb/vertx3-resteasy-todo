package com.ekocaman.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class AppTest {
    private Vertx vertx;
    private HttpServer server;

    @Before
    public void before(TestContext context) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(RestVerticle.class.getName());
        vertx.deployVerticle(TodoBackend.class.getName());
    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void getAllTasksSuccessfully(TestContext context) {
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();
        client.getNow(8080, "localhost", "/api/tasks", resp -> {
            resp.bodyHandler(body -> {
                context.assertNotNull(body);

                TaskList tasks = Json.decodeValue(body.toString(), TaskList.class);
                context.assertNotNull(tasks);
                context.assertEquals(tasks.getTasks().isEmpty(), false);

                client.close();
                async.complete();
            });
        });
    }

    @Test
    public void addNewTaskSuccessfully(TestContext context) {
        Task task = new Task();
        task.setMessage("NewTask");
        task.setCompleted(false);
        String json = Json.encode(task);

        HttpClient client = vertx.createHttpClient();
        Async async = context.async();
        HttpClientRequest httpClientRequest = client.post(8080, "localhost", "/api/tasks", resp -> {
            resp.bodyHandler(body -> {
                context.assertNotNull(body);

                TaskList tasks = Json.decodeValue(body.toString(), TaskList.class);
                context.assertNotNull(tasks);
                context.assertEquals(tasks.getTasks().isEmpty(), false);

                boolean contains = false;
                for (Task t : tasks.getTasks()) {
                    if (t.getMessage().equals(task.getMessage())) {
                        contains = true;
                        break;
                    }
                }
                context.assertTrue(contains);

                client.close();
                async.complete();
            });
        });
        httpClientRequest.putHeader("Content-Type", "application/json");
        httpClientRequest.putHeader("Content-Length", String.valueOf(json.length()));
        httpClientRequest.write(json);
        httpClientRequest.end();
    }

    @Test
    public void updateTaskSuccessfully(TestContext context) {
        Task task = new Task();
        task.setId("1");
        task.setMessage("UpdateTask");
        task.setCompleted(false);
        String json = Json.encode(task);

        HttpClient client = vertx.createHttpClient();
        Async async = context.async();

        // Create new Task
        {
            HttpClientRequest httpClientRequest = client.post(8080, "localhost", "/api/tasks", resp -> {
            });
            httpClientRequest.putHeader("Content-Type", "application/json");
            httpClientRequest.putHeader("Content-Length", String.valueOf(json.length()));
            httpClientRequest.write(json);
            httpClientRequest.end();
        }

        // Update existing Task
        {
            HttpClientRequest httpClientRequest = client.put(8080, "localhost", "/api/tasks", resp -> {
                resp.bodyHandler(body -> {
                    context.assertNotNull(body);

                    TaskList tasks = Json.decodeValue(body.toString(), TaskList.class);
                    context.assertNotNull(tasks);
                    context.assertEquals(tasks.getTasks().isEmpty(), false);

                    client.close();
                    async.complete();
                });
            });
            httpClientRequest.putHeader("Content-Type", "application/json");
            httpClientRequest.putHeader("Content-Length", String.valueOf(json.length()));
            httpClientRequest.write(json);
            httpClientRequest.end();
        }
    }
}
