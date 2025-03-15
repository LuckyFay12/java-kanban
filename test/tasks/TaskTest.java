package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private Task task1;
    private Task task2;

    @BeforeEach
    public void init() {
        task1 = new Task(0,"Магазин", TaskStatus.NEW,"Купить хлеба",  Duration.ofMinutes(60), LocalDateTime.of(2025, 02, 03, 18, 0));
        task2 = new Task(1, "Кот", TaskStatus.NEW,"Покормить кота",  Duration.ofMinutes(15), LocalDateTime.of(2025, 02, 03, 16, 0));
    }

    @Test
    void testToString() {
        String expected = "Task{id=0, name='Магазин', description='Купить хлеба', status=NEW, taskType=TASK, duration=60, startTime=03.02.2025 18:00, endTime=03.02.2025 19:00}";
        String actually = task1.toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testHashCode() {
        int expected = -997525844;
        int actually = task1.hashCode();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testGetDescription() {
        String expectedDescription = "Купить хлеба";
        String actualDescription = task1.getDescription();
        Assertions.assertEquals(expectedDescription, actualDescription);
    }

    @Test
    void testSetDescription() {
        String expectedDescription = "Купить молока";
        task1.setDescription("Купить молока");
        String actualDescription = task1.getDescription();
        Assertions.assertEquals(expectedDescription, actualDescription);
    }

    @Test
    void testGetId() {
        int expectedId = 0;
        int actualId = task1.getId();
        Assertions.assertEquals(expectedId, actualId);
    }

    @Test
    void testSetId() {
        int expectedId = 1;
        task1.setId(1);
        int actualId = task1.getId();
        Assertions.assertEquals(expectedId, actualId);
    }

    @Test
    void testGetName() {
        String expectedName = "Магазин";
        String actualName = task1.getName();
        Assertions.assertEquals(expectedName, actualName);
    }

    @Test
    void testSetName() {
        String expectedName = "Супермаркет";
        task1.setName(expectedName);
        String actualName = task1.getName();
        Assertions.assertEquals(expectedName, actualName);
    }

    @Test
    void testGetStatus() {
        TaskStatus expected = TaskStatus.NEW;
        TaskStatus actually = task1.getStatus();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testSetStatus() {
        TaskStatus expected = TaskStatus.DONE;
        task1.setStatus(TaskStatus.DONE);
        TaskStatus actually = task1.getStatus();
        Assertions.assertEquals(expected, actually);
    }
}