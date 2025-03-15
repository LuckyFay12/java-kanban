package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.CollisionTaskTimeException;
import exceptions.TaskNotFoundException;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpTaskHandler extends BaseHttpHandler {

    public HttpTaskHandler(TaskManager taskManager, Gson jsonMapper) {
        super(taskManager, jsonMapper);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            switch (method) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                    break;
                default:
                    sendErrorResponse(exchange, 405, String.format("Обработка метода %s не предусмотрена", method));
            }
        } catch (TaskNotFoundException e) {
            sendErrorResponse(exchange, 404, e.getMessage());
        } catch (CollisionTaskTimeException e) {
            sendErrorResponse(exchange, 406, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        URI requestUri = exchange.getRequestURI();
        String path = requestUri.getPath();
        String[] urlParts = path.split("/");
        if (urlParts.length == 2) {
            List<Task> tasks = taskManager.findAllTasks();
            String json = jsonMapper.toJson(tasks);
            sendText(exchange, json, 200);
        } else if (urlParts.length == 3) {
            // проверка на целое число
            String idStr = urlParts[2];
            if (isInteger(idStr)) {
                Integer id = Integer.valueOf(idStr);
                Task taskById = taskManager.findTaskById(id);
                String json = jsonMapper.toJson(taskById);
                sendText(exchange, json, 200);
            } else {
                System.out.println("Введите целое число");
            }
        }
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        byte[] bodyBytes = exchange.getRequestBody().readAllBytes();
        String bodyString = new String(bodyBytes, StandardCharsets.UTF_8);
        Task task = jsonMapper.fromJson(bodyString, Task.class);
        if (task.getId() == null) {
            Task createdTask = taskManager.createTask(task);
            String json = jsonMapper.toJson(createdTask);
            sendText(exchange, json, 201);
        } else {
            Task updatedTask = taskManager.updateTask(task);
            String json = jsonMapper.toJson(updatedTask);
            sendText(exchange, json, 201);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        URI requestUri = exchange.getRequestURI();
        String path = requestUri.getPath();
        String[] urlParts = path.split("/");
        Integer id = Integer.valueOf(urlParts[2]);
        Task deletedTask = taskManager.deleteTask(id);
        String json = jsonMapper.toJson(deletedTask);
        sendText(exchange, json, 200);
    }
}
