package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HttpPrioritizedTasksHandler extends BaseHttpHandler {

    public HttpPrioritizedTasksHandler(TaskManager taskManager, Gson jsonMapper) {
        super(taskManager, jsonMapper);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            if (method.equals("GET")) {
                getPrioritizedTasks(exchange);
            } else {
                sendErrorResponse(exchange, 405, String.format("Обработка метода %s не предусмотрена", method));
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void getPrioritizedTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getPrioritizedTasks();
        String json = jsonMapper.toJson(tasks);
        sendText(exchange, json, 200);
    }
}

