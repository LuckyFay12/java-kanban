import manager.*;
import tasks.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), new File("data.csv"));

        Task task = new Task(1, "Task 1", TaskStatus.NEW, "Description1", Duration.ofMinutes(20), LocalDateTime.of(2023, 10, 5, 10, 0));
        Epic epic1 = new Epic(2, "Учеба", TaskStatus.NEW, "Послушать лекции", Duration.ofMinutes(0), null);
        SubTask subTask1 = new SubTask(3, "История", TaskStatus.NEW, "Послушать лекции", Duration.ofHours(1), LocalDateTime.of(2023, 10, 6, 10, 20), epic1.getId());
        SubTask subTask2 = new SubTask(4, "Java", TaskStatus.NEW, "Посмотреть вебинар", Duration.ofHours(1), LocalDateTime.of(2023, 10, 6, 11, 20), epic1.getId());
        taskManager.createTask(task);
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1, epic1);
        taskManager.createSubTask(subTask2, epic1);
        System.out.println(taskManager.getPrioritizedTasks());
        task.setStatus(TaskStatus.IN_PROGRESS);
        System.out.println(taskManager.updateTask(task));
        System.out.println(taskManager.getPrioritizedTasks());
    }
}











