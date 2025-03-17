package server;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exceptions.TaskNotFoundException;
import http.HttpTaskServer;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpTaskServerTest {

    private TaskManager taskManager;
    private HttpTaskServer taskServer;
    private Gson gsonMapper;

    public HttpTaskServerTest() throws IOException {
        this.taskManager = Managers.getDefault();
        this.taskServer = new HttpTaskServer("localhost", 8080, taskManager);
        this.gsonMapper = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @BeforeEach
    public void setUp() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop(1);
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gsonMapper.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = taskManager.findAllTasks();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Task1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }


    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Testing task 1", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 3, 11, 14, 15));
        String taskJson = gsonMapper.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode(), "Задача не создана");

        Task createdTask = gsonMapper.fromJson(response.body(), Task.class);
        int taskId = createdTask.getId();
        Task updatedTask = new Task("Task2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 3, 11, 14, 15));
        updatedTask.setId(taskId);
        String updatedTaskJson = gsonMapper.toJson(updatedTask);

        url = URI.create("http://localhost:8080/tasks/" + taskId);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode(), "Задача не обновлена");

        List<Task> tasks = taskManager.findAllTasks();
        Assertions.assertEquals(1, tasks.size(), "Некорректное количество задач");
        Assertions.assertEquals("Task2", updatedTask.getName(), "Некорректное имя задачи");
    }

    @Test
    public void testIntersectionOfTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Testing task 1", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 3, 11, 14, 15));
        String taskJson = gsonMapper.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task("Task2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 3, 11, 14, 15));
        String taskJson2 = gsonMapper.toJson(task2);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(406, response.statusCode(), "Ожидался код 406");
    }

    @Test
    public void testFindAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Testing task 1", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 3, 11, 14, 15));
        Task task2 = new Task("Task2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 3, 14, 14, 20));
        String taskJson1 = gsonMapper.toJson(task1);
        String taskJson2 = gsonMapper.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode(), "Задачи не добавлены");
        List<Task> tasks = taskManager.findAllTasks();
        Assertions.assertEquals(2, tasks.size(), "Некорректное количество задач");
    }

    @Test
    public void testFindTaskById() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Testing task 1", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 3, 11, 14, 15));
        taskManager.createTask(task);
        String taskJson1 = gsonMapper.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode(), "Задачи не добавлены");
        Assertions.assertEquals(task, taskManager.findTaskById(task.getId()), "Задачи не совпадают");
    }

    @Test
    public void testFindUncreatedTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThrows(TaskNotFoundException.class, () -> taskManager.findTaskById(0));
        Assertions.assertEquals(404, response.statusCode(), "Задачи не должно быть");
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Testing task 1", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 3, 11, 14, 15));
        String taskJson = gsonMapper.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/0");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode(), "Задача не удалена");
    }

    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "DescriptionEpic");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask", "Testing subtask ", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epic.getId());
        String subTaskJson = gsonMapper.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        List<SubTask> subTasksFromManager = taskManager.findAllSubTasks();
        Assertions.assertNotNull(subTasksFromManager, "Подзадачи не возвращаются");
        Assertions.assertEquals(1, subTasksFromManager.size(), "Некорректное количество подзадач");
        Assertions.assertEquals("SubTask", subTasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testUpdateSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "DescriptionEpic");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask", "Testing subTask 1", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 3, 11, 14, 15), epic.getId());
        String subTaskJson = gsonMapper.toJson(subTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode(), "Подзадача не создана");
        SubTask createdSubTask = gsonMapper.fromJson(response.body(), SubTask.class);
        int subTaskId = createdSubTask.getId();

        SubTask updatedSubTask = new SubTask("SubTask Update", "Testing subTask 1", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 3, 11, 14, 15), epic.getId());
        updatedSubTask.setId(subTaskId);
        String updatedSubTaskJson = gsonMapper.toJson(updatedSubTask);

        url = URI.create("http://localhost:8080/subtasks/" + subTaskId);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(updatedSubTaskJson))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode(), "Подзадача не обновлена");

        List<SubTask> subTasks = taskManager.findAllSubTasks();
        Assertions.assertEquals(1, subTasks.size(), "Некорректное количество подзадач");
        Assertions.assertEquals("SubTask Update", updatedSubTask.getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testFindAllSubTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "DescriptionEpic");
        taskManager.createEpic(epic);
        SubTask subTask1 = new SubTask("SubTask1", "Testing subtask 1", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 3, 11, 14, 15), epic.getId());
        SubTask subTask2 = new SubTask("SubTask2", "Testing subtask 2", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 3, 14, 14, 20), epic.getId());
        String subTask1Json = gsonMapper.toJson(subTask1);
        String subTask2Json = gsonMapper.toJson(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTask1Json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTask2Json))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode(), "Подзадачи не добавлены");

        List<SubTask> subTasks = taskManager.findAllSubTasks();
        Assertions.assertEquals(2, subTasks.size(), "Некорректное количество подзадач");

    }

    @Test
    public void testIntersectionOfSubTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "DescriptionEpic");
        taskManager.createEpic(epic);
        SubTask subTask1 = new SubTask("SubTask1", "Testing subtask 1", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 3, 11, 14, 15), epic.getId());
        String subTask1Json = gsonMapper.toJson(subTask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTask1Json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask subTask2 = new SubTask("SubTask2", "Testing subtask 2", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 3, 11, 14, 15), epic.getId());
        String subTask2Json = gsonMapper.toJson(subTask2);

        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTask2Json))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response.statusCode(), "Ожидался код 406");
    }

    @Test
    public void testFindSubTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "DescriptionEpic");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask", "Testing subtask", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 3, 11, 14, 15), epic.getId());
        taskManager.createSubTask(subTask, epic);
        String subTaskJson = gsonMapper.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/subtasks/" + subTask.getId());
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode(), "Подзадачи не добавлены");
        Assertions.assertEquals(subTask, taskManager.findSubTaskById(subTask.getId()), "Подзадачи не совпадают");
    }

    @Test
    public void testFindUncreatedSubTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThrows(TaskNotFoundException.class, () -> taskManager.findSubTaskById(0));
        Assertions.assertEquals(404, response.statusCode(), "Подзадачи не должно быть");
    }

    @Test
    public void testDeleteSubTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "DescriptionEpic");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask", "Testing subtask", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 3, 11, 14, 15), epic.getId());
        taskManager.createSubTask(subTask, epic);
        String subTaskJson = gsonMapper.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/subtasks/" + subTask.getId());
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode(), "Подзадача не удалена");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "DescriptionEpic");
        String epicJson = gsonMapper.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = taskManager.findAllEpics();
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество эпиков");
        Assertions.assertEquals("Epic", tasksFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void testFindAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "DescriptionEpic1");
        Epic epic2 = new Epic("Epic2", "DescriptionEpic2");
        String epicJson1 = gsonMapper.toJson(epic1);
        String epicJson2 = gsonMapper.toJson(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson1))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson2))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode(), "Эпики не добавлены");

        List<Epic> epicsFromMananger = taskManager.findAllEpics();
        Assertions.assertEquals(2, epicsFromMananger.size(), "Некорректное количество эпиков");
    }

    @Test
    public void testFindEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "DescriptionEpic");
        taskManager.createEpic(epic);
        String epicJson = gsonMapper.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode(), "Эпики не добавлены");
        Assertions.assertEquals(epic, taskManager.findEpicById(epic.getId()), "Эпики не совпадают");
    }

    @Test
    public void testFindUncreatedEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThrows(TaskNotFoundException.class, () -> taskManager.findEpicById(0));
        Assertions.assertEquals(404, response.statusCode(), "Задачи не должно быть");
    }

    @Test
    public void testFindEpicSubTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "DescriptionEpic");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask", "Testing subtask", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 3, 11, 14, 15), epic.getId());
        taskManager.createSubTask(subTask, epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/0/subTasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode(), "Ожидался код 200");
        Assertions.assertEquals(epic, taskManager.findEpicById(epic.getId()), "Эпики не совпадают");

        List<SubTask> subTasksByEpic = taskManager.getListSubTasksByEpic(epic.getId());
        Assertions.assertEquals(1, subTasksByEpic.size(), "Некорректное количество подзадач");
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "DescriptionEpic");
        String epicJson = gsonMapper.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/epics/0");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode(), "Эпик не удален");
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "DescriptionEpic");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask", "Testing subtask", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 3, 11, 14, 15), epic.getId());
        taskManager.createSubTask(subTask, epic);
        Task task = new Task("Task", "Testing task", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2026, 3, 11, 14, 15));
        taskManager.createTask(task);

        taskManager.findEpicById(0);
        taskManager.findSubTaskById(1);
        taskManager.findTaskById(2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode(), "Ожидался код 200");
        Assertions.assertEquals(3, taskManager.getHistory().size(), "Ответ не совпадает с ожидаемым");
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Testing task 1", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 3, 11, 14, 15));
        taskManager.createTask(task1);
        Task task2 = new Task("Task2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, 3, 12, 15, 15));
        taskManager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode(), "Ожидался код 200");

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        Assertions.assertEquals(2, prioritizedTasks.size(), "Некорректное количество задач");
        Assertions.assertEquals(task1, prioritizedTasks.get(0));
    }
}

