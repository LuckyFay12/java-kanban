package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private Epic epic;
    private SubTask subTask1;
    private SubTask subTask2;

    @BeforeEach
    public void init() {
        LocalDateTime endTime = LocalDateTime.of(2025, 02, 04, 18, 0).plus(Duration.ofMinutes(15));
        epic = new Epic(0, "Лекции", TaskStatus.NEW, "Послушать лекции", Duration.ofMinutes(15), LocalDateTime.of(2025, 02, 04, 18, 0), endTime);
        subTask1 = new SubTask("Пол", "Помыть пол", TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.of(2025, 02, 03, 18, 0), epic.getId());
        subTask2 = new SubTask("Пыль", "Вытереть пыль", TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.of(2025, 02, 03, 16, 0), epic.getId());
    }

    @Test
    void testToString() {
        String expected = "Epic{id=0, name=Лекции, subTasksId=[], description=Послушать лекции, status=NEW, taskType=EPIC, duration=15, startTime=04.02.2025 18:00, endTime=04.02.2025 18:15}";
        String actually = epic.toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void getSubtasks() {
        epic.addSubTask(subTask1);
        epic.addSubTask(subTask2);
        List<Integer> expectedSubTasksId = new ArrayList<>();
        expectedSubTasksId.add(subTask1.getId());
        expectedSubTasksId.add(subTask2.getId());
        List<Integer> actualSubTasksId = epic.getSubtasks();
        Assertions.assertEquals(expectedSubTasksId, actualSubTasksId);
    }

    @Test
    void delSubTask() {
        epic.addSubTask(subTask1);
        epic.addSubTask(subTask2);
        List<Integer> expected = new ArrayList<>();
        expected.add(subTask1.getId());
        epic.delSubTask(subTask2.getId());
        List<Integer> actually = epic.getSubtasks();
        Assertions.assertEquals(expected, actually);
    }
}