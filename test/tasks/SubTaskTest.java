package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    private SubTask subTask;

    @BeforeEach
    public void init() {
        subTask = new SubTask("Кот", "Покормить кота", TaskStatus.IN_PROGRESS, Duration.ofMinutes(60), LocalDateTime.of(2025, 02, 03, 18, 0), 2);
    }

    @Test
    void testGetEpicId() {
        int expectedEpicId = 2;
        int actualEpicId = subTask.getEpicId();
        Assertions.assertEquals(expectedEpicId, actualEpicId);
    }

    @Test
    void testSetEpicId() {
        int expectedEpicId = 3;
        subTask.setEpicId(3);
        int actualEpicId = subTask.getEpicId();
        Assertions.assertEquals(expectedEpicId, actualEpicId);
    }

    @Test
    void testToString() {
        String expected = "SubTask{id=0, name=Кот, description=Покормить кота, status=IN_PROGRESS, taskType=TASK, duration=60, startTime=03.02.2025 18:00, endTime=03.02.2025 19:00, epicId=2}";
        String actually = subTask.toString();
        Assertions.assertEquals(expected, actually);
    }
}