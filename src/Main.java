import manager.*;
import tasks.*;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), new File("data.csv"));
        Task task1 = new Task(0, TaskType.TASK, "Магазин", TaskStatus.NEW, "Купить продукты по списку");
        Epic epic1 = new Epic(1, TaskType.EPIC, "Лекции", TaskStatus.NEW, "Послушать лекции");
        SubTask subTask1 = new SubTask(2, TaskType.SUBTASK, "История", TaskStatus.NEW, "Послушать лекции", epic1.getId());
        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.createEpic(epic1);
        fileBackedTaskManager.createSubTask(subTask1, epic1);
        System.out.println(fileBackedTaskManager.findAllTasks());
        System.out.println(fileBackedTaskManager.findAllSubTasks());
        System.out.println(fileBackedTaskManager.findAllEpics());
    }
}











