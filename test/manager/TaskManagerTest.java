package manager;

import exceptions.CollisionTaskTimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.util.ArrayList;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @BeforeEach
    abstract void initManager();

    @Test
    protected void testCreateTask() {
        String description = "Почистить зубы";
        String name = "Гигиена";
        Task task = new Task(name, description, TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 02, 03, 15, 0));
        taskManager.createTask(task);
        Task actualTask = taskManager.findTaskById(task.getId());

        Assertions.assertNotNull(actualTask.getId());
        Assertions.assertEquals(actualTask.getName(), name);
        Assertions.assertEquals(actualTask.getDescription(), description);
        Assertions.assertEquals(actualTask.getStatus(), TaskStatus.NEW);
    }

    @Test
    void updateTaskStatusToInProgress() {
        Task task = new Task("Магазин", "Купить хлеба", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 02, 03, 15, 30));
        taskManager.createTask(task);
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);

        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.findTaskById(task.getId()).getStatus());
    }

    @Test
    void listWithTasksShouldBeOtherAfterDeleteTaskById() {
        Task task1 = new Task("Магазин", "Купить хлеба", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 02, 03, 15, 0));
        Task task2 = new Task("Кот", "Покормить кота", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 02, 03, 18, 0));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        ArrayList<Task> tasksBeforeDelete = new ArrayList<>(taskManager.findAllTasks());
        taskManager.deleteTask(task2.getId());
        ArrayList<Task> tasksAfterDelete = new ArrayList<>(taskManager.findAllTasks());

        Assertions.assertEquals(tasksBeforeDelete.size(), 2);
        Assertions.assertEquals(tasksAfterDelete.size(), 1);
        Assertions.assertNotSame(tasksBeforeDelete, tasksAfterDelete);
    }

    @Test
    void testFindAllTasks() {
        Task task1 = new Task("Магазин", "Купить хлеба", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 02, 03, 15, 0));
        Task task2 = new Task("Кот", "Покормить кота", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 02, 03, 18, 0));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        ArrayList<Task> tasks = new ArrayList<>(taskManager.findAllTasks());

        Assertions.assertNotNull(tasks);
        Assertions.assertTrue(tasks.size() == 2);
    }

    @Test
    void testFindTaskById() {
        Task task1 = new Task("Магазин", "Купить хлеба", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 02, 03, 15, 0));
        taskManager.createTask(task1);

        Assertions.assertEquals(taskManager.findTaskById(task1.getId()), task1);
    }


    @Test
    void checkThatTheListIsEmptyAfterDeletingAllTasks() {
        Task task1 = new Task("Магазин", "Купить хлеба", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 02, 03, 15, 0));
        Task task2 = new Task("Кот", "Покормить кота", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 02, 03, 18, 0));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.deleteAllTasks();

        Assertions.assertTrue(taskManager.findAllTasks().isEmpty());
    }

    @Test
    void testCreateEpic() {
        String description = "Нарядить елку";
        String name = "Елка";
        Epic epic = new Epic(name, description);
        taskManager.createEpic(epic);
        Epic actualEpic = taskManager.findEpicById(epic.getId());

        Assertions.assertNotNull(actualEpic.getId());
        Assertions.assertEquals(actualEpic.getName(), name);
        Assertions.assertEquals(actualEpic.getDescription(), description);
        Assertions.assertEquals(actualEpic.getStatus(), TaskStatus.NEW);
    }

    @Test
    void updateEpicName() {
        Epic epic = new Epic("Елка", "Нарядить елку");
        taskManager.createEpic(epic);
        epic.setName("Ель");
        taskManager.updateEpic(epic);

        Assertions.assertEquals("Ель", taskManager.findEpicById(epic.getId()).getName());
    }

    @Test
    void testFindAllEpics() {
        Epic epic1 = new Epic("Елка", "Нарядить елку");
        Epic epic2 = new Epic("Салат", "Нарезать овощи");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        ArrayList<Task> epics = new ArrayList<>(taskManager.findAllEpics());

        Assertions.assertNotNull(epics);
        Assertions.assertTrue(epics.size() == 2);
    }

    @Test
    void testFindEpicById() {
        Epic epic = new Epic("Елка", "Нарядить елку");
        taskManager.createEpic(epic);

        Assertions.assertEquals(taskManager.findEpicById(epic.getId()), epic);
    }

    @Test
    void listWithEpicsShouldBeOtherAfterDeleteEpicById() {
        Epic epic1 = new Epic("Елка", "Нарядить елку");
        Epic epic2 = new Epic("Салат", "Нарезать овощи");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        ArrayList<Epic> epicsBeforeDelete = new ArrayList<>(taskManager.findAllEpics());
        taskManager.deleteEpic(epic2.getId());
        ArrayList<Task> epicsAfterDelete = new ArrayList<>(taskManager.findAllEpics());

        Assertions.assertEquals(epicsBeforeDelete.size(), 2);
        Assertions.assertEquals(epicsAfterDelete.size(), 1);
        Assertions.assertNotSame(epicsBeforeDelete, epicsAfterDelete);
    }

    @Test
    void checkThatTheListOfEpicsAndListOfSubTasksIsEmptyAfterDeletingAllEpics() {
        Epic epic1 = new Epic("Елка", "Нарядить елку");
        Epic epic2 = new Epic("Салат", "Нарезать овощи");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        SubTask subTask = new SubTask("Овощи", "Сварить морковь и картофель", TaskStatus.IN_PROGRESS, Duration.ofMinutes(60), LocalDateTime.of(2025, 02, 03, 18, 0), epic2.getId());
        taskManager.createSubTask(subTask, epic2);
        taskManager.deleteAllEpics();

        Assertions.assertTrue(taskManager.findAllEpics().isEmpty());
        Assertions.assertTrue(taskManager.findAllSubTasks().isEmpty());
    }

    @Test
    void testDeleteAllSubtasks() {
        Epic epic = new Epic("Салат", "Нарезать овощи");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Овощи", "Сварить морковь и картофель", TaskStatus.IN_PROGRESS, Duration.ofMinutes(60), LocalDateTime.of(2025, 02, 03, 18, 0), epic.getId());
        taskManager.createSubTask(subTask, epic);
        taskManager.deleteAllSubtasks();

        Assertions.assertTrue(taskManager.findAllSubTasks().isEmpty());
    }

    @Test
    void testCreateSubTask() throws IOException {
        Epic epic = new Epic("Елка", "Нарядить елку");
        taskManager.createEpic(epic);
        String description = "Купить игрушки на елку";
        String name = "Игрушки";
        SubTask subTask = new SubTask(name, description, TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.of(2025, 02, 03, 18, 0), epic.getId());
        taskManager.createSubTask(subTask, epic);
        SubTask actualSubTask = taskManager.findSubTaskById(subTask.getId());

        Assertions.assertNotNull(actualSubTask.getId());
        Assertions.assertEquals(actualSubTask.getEpicId(), epic.getId());
        Assertions.assertEquals(actualSubTask.getName(), name);
        Assertions.assertEquals(actualSubTask.getDescription(), description);
        Assertions.assertEquals(actualSubTask.getStatus(), TaskStatus.NEW);
    }

    @Test
    void updateSubTaskName() {
        Epic epic = new Epic("Салат", "Нарезать овощи");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Овощи", "Сварить морковь и картофель", TaskStatus.IN_PROGRESS, Duration.ofMinutes(60), LocalDateTime.of(2025, 02, 03, 18, 0), epic.getId());
        taskManager.createSubTask(subTask, epic);
        subTask.setName("Морковь");
        taskManager.updateEpic(epic);

        Assertions.assertEquals("Морковь", taskManager.findSubTaskById(subTask.getId()).getName());
    }

    @Test
    void testFindAllSubTasks() {
        Epic epic = new Epic("Елка", "Нарядить елку");
        taskManager.createEpic(epic);
        SubTask subTask1 = new SubTask("Игрушки", "Купить игрушки на елку", TaskStatus.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.of(2025, 02, 03, 18, 0), epic.getId());
        SubTask subTask2 = new SubTask("Кот", "Закрыть в комнате, чтобы не мешался", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 02, 03, 19, 0), epic.getId());
        taskManager.createSubTask(subTask1, epic);
        taskManager.createSubTask(subTask2, epic);
        ArrayList<Task> subTasks = new ArrayList<>(taskManager.findAllSubTasks());

        Assertions.assertTrue(subTasks.size() == 2);
    }

    @Test
    void testFindSubTaskById() {
        Epic epic = new Epic("Елка", "Нарядить елку");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Игрушки", "Купить игрушки на елку", TaskStatus.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.of(2025, 02, 03, 18, 0), epic.getId());
        taskManager.createSubTask(subTask, epic);

        Assertions.assertEquals(taskManager.findEpicById(epic.getId()), epic);
    }

    @Test
    void testDeleteSubTaskById() {
        Epic epic = new Epic("Елка", "Нарядить елку");
        taskManager.createEpic(epic);
        SubTask subTask1 = new SubTask("Игрушки", "Купить игрушки на елку", TaskStatus.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.of(2025, 02, 03, 18, 0), epic.getId());
        SubTask subTask2 = new SubTask("Кот", "Закрыть в комнате, чтобы не мешался", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 02, 03, 19, 0), epic.getId());
        taskManager.createSubTask(subTask1, epic);
        taskManager.createSubTask(subTask2, epic);
        ArrayList<SubTask> subTasksBeforeDelete = new ArrayList<>(taskManager.findAllSubTasks());
        taskManager.deleteSubTaskById(subTask2.getId());
        ArrayList<SubTask> subTasksAfterDelete = new ArrayList<>(taskManager.findAllSubTasks());

        Assertions.assertEquals(subTasksBeforeDelete.size(), 2);
        Assertions.assertEquals(subTasksAfterDelete.size(), 1);
        Assertions.assertNotSame(subTasksBeforeDelete, subTasksAfterDelete);
    }

    @Test
    void testGetSubTasksByEpicById() {
        Epic epic = new Epic("Елка", "Нарядить елку");
        taskManager.createEpic(epic);
        SubTask subTask1 = new SubTask("Игрушки", "Купить игрушки на елку", TaskStatus.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.of(2025, 02, 03, 18, 0), epic.getId());
        SubTask subTask2 = new SubTask("Кот", "Закрыть в комнате, чтобы не мешался", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 02, 03, 19, 0), epic.getId());
        taskManager.createSubTask(subTask1, epic);
        taskManager.createSubTask(subTask2, epic);
        ArrayList<SubTask> subTasks = new ArrayList<>(taskManager.findAllSubTasks());
        ArrayList<Integer> subTasksIdByEpic = new ArrayList<>(epic.getSubTasks());

        Assertions.assertEquals(subTasks.size(), subTasksIdByEpic.size());
    }

    @Test
    void updateSubTaskStatus_ShouldUpdateEpicStatus() {
        Epic epic = new Epic("Елка", "Нарядить елку");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Игрушки", "Купить игрушки на елку", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 02, 03, 18, 0), epic.getId());
        taskManager.createSubTask(subTask, epic);
        subTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubTask(subTask);

        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.findEpicById(epic.getId()).getStatus());
    }

    @Test
    void PrioritizedTasksTestException() {
        Task task1 = new Task("Магазин", "Купить хлеба", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 02, 03, 15, 0));
        Task task2 = new Task("Кот", "Покормить кота", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 02, 03, 15, 15));
        taskManager.createTask(task1);
        Exception exc = assertThrows(CollisionTaskTimeException.class, () -> taskManager.createTask(task2));
        Assertions.assertEquals("Данная задача пересекается по времени с другой задачей", exc.getMessage());
    }
}
