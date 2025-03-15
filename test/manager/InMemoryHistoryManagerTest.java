package manager;

import exceptions.TaskNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class InMemoryHistoryManagerTest {
    TaskManager taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());

    @Test
    void testGetHistory() {
        Task task = new Task("Магазин", "Купить хлеба", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 02, 03, 15, 0));
        taskManager.createTask(task);
        Epic epic = new Epic("Елка", "Нарядить елку");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Игрушки", "Купить игрушки на елку", TaskStatus.NEW, Duration.ofMinutes(20), LocalDateTime.of(2025, 02, 04, 15, 0), epic.getId());
        taskManager.createSubTask(subTask, epic);
        taskManager.findTaskById(task.getId());
        taskManager.findEpicById(epic.getId());
        taskManager.findSubTaskById(subTask.getId());
        ArrayList<Task> history = new ArrayList<>(taskManager.getHistory());
        Assertions.assertNotNull(history);
        Assertions.assertEquals(3, history.size());
    }

    @Test
    void deletedTaskShouldNotBeDisplayedInHistory() {
        Task task = new Task("Магазин", "Купить хлеба", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 02, 03, 15, 0));
        taskManager.createTask(task);
        Epic epic = new Epic("Елка", "Нарядить елку");
        taskManager.createEpic(epic);
        taskManager.deleteEpic(epic.getId());
        taskManager.deleteTask(task.getId());

        assertThrows(TaskNotFoundException.class, () -> taskManager.findTaskById(task.getId()));
        assertThrows(TaskNotFoundException.class, () -> taskManager.findEpicById(epic.getId()));

        ArrayList<Task> history = new ArrayList<>(taskManager.getHistory());
        Assertions.assertTrue(history.isEmpty());
    }

    @Test
    void historyListShouldNotExceedContainRepeatedTasks() {
        Task task = new Task("Магазин", "Купить хлеба", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 02, 03, 15, 0));
        taskManager.createTask(task);
        Epic epic = new Epic("Елка", "Нарядить елку");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Игрушки", "Купить игрушки на елку", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 01, 03, 15, 0), epic.getId());
        taskManager.createSubTask(subTask, epic);
        taskManager.findTaskById(task.getId());
        taskManager.findEpicById(epic.getId());
        taskManager.findSubTaskById(subTask.getId());
        taskManager.findTaskById(task.getId());
        taskManager.findEpicById(epic.getId());
        taskManager.findSubTaskById(subTask.getId());
        ArrayList<Task> history = new ArrayList<>(taskManager.getHistory());

        Assertions.assertEquals(3, history.size());
    }

}
