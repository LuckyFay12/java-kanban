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
        taskManager.createTask (task1);
        Task task2 = new Task("Уборка", "Пропылесосить, вытереть пыль", TaskStatus.IN_PROGRESS);
        taskManager.createTask (task2);
        Epic epic1 = new Epic("Лекции", "Послушать лекции");
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic("Рыбки", "Покормить рыбок");
        taskManager.createEpic(epic2);
        SubTask subTask1 = new SubTask("История", "Послушать лекции", TaskStatus.NEW, epic1.getId());
        taskManager.createSubTask(subTask1, epic1);
        SubTask subTask2 = new SubTask("Физика", "Сделать конспект", TaskStatus.IN_PROGRESS, epic1.getId());
        taskManager.createSubTask(subTask2, epic1);
        SubTask subTask3 = new SubTask("Корм для рыб", "Купить корм для рыб", TaskStatus.NEW, epic2.getId());
        taskManager.createSubTask(subTask3, epic2);

        System.out.println("Вывести список задач: " + taskManager.findAllTasks());
        System.out.println("Вывести задачу 'Магазин' по id: " + taskManager.findTaskById(task1.getId()));

        task1.setName("Новый магазин");
        task1.setStatus(TaskStatus.DONE);

        System.out.println("Обновить задачу 'Магазин'" + taskManager.updateTask(task1));
        System.out.println("Вывести задачу 'Магазин' по id: " + taskManager.findTaskById(task1.getId()));
        System.out.println("Удалить задачу 'Магазин' " + taskManager.deleteTask(task1.getId()));
        System.out.println("Задача удалена. Текущие задачи: " + taskManager.findAllTasks());
        taskManager.deleteAllTasks();
        System.out.println("Очистить список задач: " + taskManager.findAllTasks());

        System.out.println("Вывести список эпиков: " + taskManager.findAllEpics());
        System.out.println("Вывести список id всех подзадач эпика 'Лекции': " + taskManager.getSubTasksByEpicById(epic1.getId()));
        System.out.println("Вывести эпик 'Рыбки' по id " + taskManager.findEpicById(epic2.getId()));
        System.out.println("Вывести подзадачу 'История' по id " + taskManager.findSubTaskById(subTask1.getId()));

        subTask3.setDescription("Корм куплен");
        subTask3.setStatus(TaskStatus.DONE);
        System.out.println("Обновить подзадачу 'Корм для рыб'" + taskManager.updateSubTask(subTask3));
        System.out.println("Вывести эпик 'Рыбки' по id " + taskManager.findEpicById(epic2.getId()));

        System.out.println("История:" + taskManager.getHistory());
        System.out.println("Добавляем просмотры еще 6 эпиков и подзадач.");
        taskManager.findEpicById(epic2.getId());
        taskManager.findEpicById(epic2.getId());
        taskManager.findEpicById(epic2.getId());
        taskManager.findEpicById(epic2.getId());
        taskManager.findSubTaskById(subTask1.getId());
        taskManager.findSubTaskById(subTask1.getId());
        System.out.println("История:" + taskManager.getHistory());

    }
}
