package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.PrimeNumberException;
import exceptions.TaskNotFoundException;
import manager.TaskManager;
import tasks.Epic;
import tasks.SubTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpEpicHandler extends BaseHttpHandler {

    public HttpEpicHandler(TaskManager taskManager, Gson jsonMapper) {
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
        } catch (PrimeNumberException e) {
            sendErrorResponse(exchange, 400, e.getMessage());
        } catch (TaskNotFoundException e) {
            sendErrorResponse(exchange, 404, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String[] urlParts = getPathParts(exchange);
        if (urlParts.length == 2) {
            List<Epic> epics = taskManager.findAllEpics();
            String json = jsonMapper.toJson(epics);
            sendText(exchange, json, 200);
        } else if (urlParts.length == 3) {
            // проверка на целое число
            String idStr = urlParts[2];
            if (isInteger(idStr)) {
                Integer id = Integer.valueOf(idStr);
                Epic epicById = taskManager.findEpicById(id);
                String json = jsonMapper.toJson(epicById);
                sendText(exchange, json, 200);
            } else {
                throw new PrimeNumberException("Введите простое число");
            }
        } else {
            String idStr = urlParts[2];
            if (isInteger(idStr)) {
                Integer id = Integer.valueOf(idStr);
                List<SubTask> subTasksByEpic = taskManager.getListSubTasksByEpic(id);
                String json = jsonMapper.toJson(subTasksByEpic);
                sendText(exchange, json, 200);
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
        Epic epic = jsonMapper.fromJson(bodyString, Epic.class);
        Epic createdEpic = taskManager.createEpic(epic);
        String json = jsonMapper.toJson(createdEpic);
        sendText(exchange, json, 201);
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String[] urlParts = getPathParts(exchange);
        Integer id = Integer.valueOf(urlParts[2]);
        Epic deletedEpic = taskManager.deleteEpic(id);
        String json = jsonMapper.toJson(deletedEpic);
        sendText(exchange, json, 200);
    }
}




