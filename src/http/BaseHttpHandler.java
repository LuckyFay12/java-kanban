package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.ErrorResponse;
import manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    protected final Gson jsonMapper;
    protected TaskManager taskManager;

    protected BaseHttpHandler(TaskManager taskManager,Gson jsonMapper) {
        this.taskManager = taskManager;
        this.jsonMapper = jsonMapper;
    }

    protected void sendText(HttpExchange h, String text, Integer code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(message, statusCode, exchange.getRequestURI().getPath());
        String json = jsonMapper.toJson(errorResponse);
        sendText(exchange, json, statusCode);
    }
}
