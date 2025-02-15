package manager;

import org.junit.jupiter.api.Test;
import tasks.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static java.io.File.createTempFile;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @Test
    void testLoadFromFile() throws IOException {
        File testFile = File.createTempFile("data-", ".csv");
        List<String> lines = List.of("1,TASK,Task,NEW,Description1",
                "2,EPIC,Epic,NEW,Description2",
                "3,SUBTASK,SubTask,NEW,Description3,2");
        Files.write(testFile.toPath(), lines);
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(testFile);
        assertNotNull(manager.findAllTasks(), "Task должен быть загружен");
        assertNotNull(manager.findAllEpics(), "Epic должен быть загружен");
        assertNotNull(manager.findAllSubTasks(), "SubTask должен быть загружен");
    }

    @Test
    void testLoadEmptyFile() throws IOException {
        File testFile = File.createTempFile("data-", ".csv");
        Files.write(testFile.toPath(), List.of());
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(testFile);
        assertTrue(manager.findAllTasks().isEmpty());
        assertTrue(manager.findAllEpics().isEmpty());
        assertTrue(manager.findAllSubTasks().isEmpty());
    }

    @Test
    void testCreateSubTask() throws IOException {
        File testFile = File.createTempFile("data-", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(new InMemoryHistoryManager(), testFile);
        Epic epic = new Epic(1, TaskType.EPIC, "Уборка", TaskStatus.NEW, "Помыть пол");
        manager.createEpic(epic);
        SubTask subTask = new SubTask(2, TaskType.SUBTASK, "Пыть", TaskStatus.NEW, "Вытереть пыль", 1);
        manager.createSubTask(subTask, epic);
        List<String> fileContent = Files.readAllLines(testFile.toPath());
        assertEquals(2, fileContent.size(), "Файл должен содержать две строки.");
    }
}