import manager.*;
import tasks.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), new File("data.csv"));

        Task task = new Task(1, TaskType.TASK, "Task 1", TaskStatus.NEW, "Description1", Duration.ofMinutes(20), LocalDateTime.of(2023, 10, 5, 10, 0));
        Epic epic1 = new Epic(2, TaskType.EPIC, "Учеба", TaskStatus.NEW, "Послушать лекции", Duration.ofMinutes(0), null);
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask(3, TaskType.SUBTASK, "История", TaskStatus.NEW, "Послушать лекции", Duration.ofHours(1), LocalDateTime.of(2023, 10, 6, 10, 20), epic1.getId());
        SubTask subTask2 = new SubTask(4, TaskType.SUBTASK, "Java", TaskStatus.NEW, "Посмотреть вебинар", Duration.ofHours(1), LocalDateTime.of(2023, 10, 6, 11, 20), epic1.getId());
        taskManager.createTask(task);
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1, epic1);
        taskManager.createSubTask(subTask2, epic1);
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);
        System.out.println(taskManager.getPrioritizedTasks());
        taskManager.deleteEpic(epic1.getId());
        System.out.println(taskManager.getPrioritizedTasks());
    }
}











