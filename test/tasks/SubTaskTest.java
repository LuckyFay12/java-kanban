package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    private SubTask subTask;

    @BeforeEach
    public void init() {
        subTask = new SubTask("Кот", "Покормить кота", TaskStatus.IN_PROGRESS, 2);
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
        String expected = "SubTask{epicId=2, name=Кот, description=Покормить кота, status=IN_PROGRESS}";
        String actually = subTask.toString();
        Assertions.assertEquals(expected, actually);
    }
}