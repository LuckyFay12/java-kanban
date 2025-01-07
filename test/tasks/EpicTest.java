package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class EpicTest {
    private Epic epic;
    private SubTask subTask1;
    private SubTask subTask2;

    @BeforeEach
    public  void init() {
        epic = new Epic("Уборка", "Генеральная уборка");
        subTask1 = new SubTask("Пол", "Помыть пол", TaskStatus.NEW, 0);
        subTask2 = new SubTask("Пыль", "Вытереть пыль", TaskStatus.NEW, 0);
    }

    @Test
    void testToString() {
        String expected = "Epic{id=0, name=Уборка, SubTasksId=[], description=Помыть пол, status=NEW}";
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