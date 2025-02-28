package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.io.File.createTempFile;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    File testFile;

    @BeforeEach
    void initManager() {
        try {
            testFile = File.createTempFile("data-", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.taskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), testFile);
    }

    @Test
    void testLoadFromFile() throws IOException {

        List<String> lines = List.of(
                "1,TASK,Task,NEW,Description1,30,03.02.2025 15:00",
                "2,EPIC,Epic,NEW,Description2,45,02.01.2025 15:15",
                "3,SUBTASK,SubTask,NEW,Description3,45,02.01.2025 15:15,2"
        );
        Files.write(testFile.toPath(), lines);
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(testFile);
        assertNotNull(manager.findAllTasks(), "Task должен быть загружен");
        assertNotNull(manager.findAllEpics(), "Epic должен быть загружен");
        assertNotNull(manager.findAllSubTasks(), "SubTask должен быть загружен");
    }

    @Test
    void testLoadEmptyFile() throws IOException {
        Files.write(testFile.toPath(), List.of());
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(testFile);
        assertTrue(manager.findAllTasks().isEmpty());
        assertTrue(manager.findAllEpics().isEmpty());
        assertTrue(manager.findAllSubTasks().isEmpty());
    }


    @Test
    void testCreateSubTask() throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(new InMemoryHistoryManager(), testFile);
        Epic epic = new Epic(1, TaskType.EPIC, "Уборка", TaskStatus.NEW, "Помыть пол", Duration.ofMinutes(0), null);
        manager.createEpic(epic);
        SubTask subTask = new SubTask(2, TaskType.SUBTASK, "Пыть", TaskStatus.NEW, "Вытереть пыль", Duration.ofMinutes(45), LocalDateTime.of(2025, 01, 02, 15, 15), 1);
        manager.createSubTask(subTask, epic);
        List<String> fileContent = Files.readAllLines(testFile.toPath());
        assertEquals(2, fileContent.size(), "Файл должен содержать две строки.");
    }
}