import manager.Managers;
import manager.TaskManager;
import tasks.Task;
import tasks.Epic;
import tasks.SubTask;
import tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Магазин", "Купить продукты по списку", TaskStatus.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("Уборка", "Пропылесосить, вытереть пыль", TaskStatus.IN_PROGRESS);
        taskManager.createTask(task2);
        Epic epic1 = new Epic("Лекции", "Послушать лекции");
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic("Рыбки", "Покормить рыбок");
        taskManager.createEpic(epic2);
        SubTask subTask1 = new SubTask("История", "Послушать лекции", TaskStatus.NEW, epic1.getId());
        taskManager.createSubTask(subTask1, epic1);
        SubTask subTask2 = new SubTask("Физика", "Сделать конспект", TaskStatus.IN_PROGRESS, epic1.getId());
        taskManager.createSubTask(subTask2, epic1);
        SubTask subTask3 = new SubTask("Философия", "Сделать конспект", TaskStatus.IN_PROGRESS, epic1.getId());
        taskManager.createSubTask(subTask3, epic1);

        taskManager.findTaskById(task1.getId());
        taskManager.findTaskById(task2.getId());
        taskManager.findEpicById(epic1.getId());
        taskManager.findEpicById(epic2.getId());
        taskManager.findSubTaskById(subTask1.getId());
        taskManager.findSubTaskById(subTask2.getId());
        taskManager.findSubTaskById(subTask3.getId());
        System.out.println("История просмотров:" + taskManager.getHistory());

        taskManager.findSubTaskById(subTask1.getId());
        taskManager.findSubTaskById(subTask2.getId());
        taskManager.findSubTaskById(subTask3.getId());
        taskManager.findTaskById(task1.getId());
        taskManager.findTaskById(task2.getId());
        taskManager.findEpicById(epic1.getId());
        taskManager.findEpicById(epic2.getId());
        System.out.println("История просмотров:" + taskManager.getHistory());

        taskManager.findSubTaskById(subTask1.getId());
        System.out.println("История просмотров:" + taskManager.getHistory());

        taskManager.deleteTask(task2.getId());
        System.out.println("История просмотров:" + taskManager.getHistory());

        taskManager.deleteEpic(epic1.getId());
        System.out.println("История просмотров:" + taskManager.getHistory());

    }
}
