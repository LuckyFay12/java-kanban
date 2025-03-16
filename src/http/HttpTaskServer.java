package http;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private final String hostname;
    private final int port;
    private final HttpServer httpServer;
    private final TaskManager taskManager;
    private final Gson jsonMapper;

    public HttpTaskServer(String hostname, int port, TaskManager taskManager) throws IOException {
        this.port = port;
        this.hostname = hostname;
        this.taskManager = taskManager;
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(hostname, port), 0);

        this.jsonMapper = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        registerHandlers();
    }

    private void registerHandlers() {
        httpServer.createContext("/tasks", new HttpTaskHandler(taskManager, jsonMapper));
        httpServer.createContext("/subtasks", new HttpSubTaskHandler(taskManager, jsonMapper));
        httpServer.createContext("/epics", new HttpEpicHandler(taskManager, jsonMapper));
        httpServer.createContext("/prioritized", new HttpPrioritizedTasksHandler(taskManager, jsonMapper));
        httpServer.createContext("/history", new HttpHistoryHandler(taskManager, jsonMapper));
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + hostname + ":" + port);
    }

    public void stop(int delay) {
        httpServer.stop(delay);
        System.out.println("HTTP-сервер остановлен");
    }
}
