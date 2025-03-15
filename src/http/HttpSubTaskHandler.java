package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.CollisionTaskTimeException;
import exceptions.TaskNotFoundException;
import manager.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpSubTaskHandler extends BaseHttpHandler {

    public HttpSubTaskHandler(TaskManager taskManager, Gson jsonMapper) {
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
            List<SubTask> subTasks = taskManager.findAllSubTasks();
            String json = jsonMapper.toJson(subTasks);
            sendText(exchange, json, 200);
        } else if (urlParts.length == 3) {
            // проверка на целое число
            String idStr = urlParts[2];
            if (isInteger(idStr)) {
                Integer id = Integer.valueOf(idStr);
                Task subTaskById = taskManager.findSubTaskById(id);
                String json = jsonMapper.toJson(subTaskById);
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
        SubTask subTask = jsonMapper.fromJson(bodyString, SubTask.class);
        if (subTask.getId() == null) {
            Integer epicId = subTask.getEpicId();
            Epic epic = taskManager.findEpicById(epicId);
            SubTask createdSubTask = taskManager.createSubTask(subTask, epic);
            String json = jsonMapper.toJson(createdSubTask);
            sendText(exchange, json, 201);
        } else {
            SubTask updatedSubTask = taskManager.updateSubTask(subTask);
            String json = jsonMapper.toJson(updatedSubTask);
            sendText(exchange, json, 201);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        URI requestUri = exchange.getRequestURI();
        String path = requestUri.getPath();
        String[] urlParts = path.split("/");
        Integer id = Integer.valueOf(urlParts[2]);
        SubTask deletedSubTask = taskManager.deleteSubTaskById(id);
        String json = jsonMapper.toJson(deletedSubTask);
        sendText(exchange, json, 200);
    }
}


