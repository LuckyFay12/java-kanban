package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HttpHistoryHandler extends BaseHttpHandler {

    public HttpHistoryHandler(TaskManager taskManager, Gson jsonMapper) {
        super(taskManager, jsonMapper);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            if (method.equals("GET")) {
                getHistory(exchange);
            } else {
                sendErrorResponse(exchange, 405, String.format("Обработка метода %s не предусмотрена", method));
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void getHistory(HttpExchange exchange) throws IOException {
        List<Task> history = taskManager.getHistory();
        String json = jsonMapper.toJson(history);
        sendText(exchange, json, 200);
    }
}
