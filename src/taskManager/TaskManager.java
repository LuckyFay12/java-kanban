package taskManager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public interface TaskManager {
    Task createTask(Task task);

    Task updateTask(Task task);

    Task deleteTask(Integer id);

    List<Task> findAllTasks();

    Task findTaskById(Integer id);

    List<Task> getHistory();

    void deleteAllTasks();

    Epic createEpic(Epic epic);

    Epic updateEpic(Epic epic);

    List<Epic> findAllEpics();

    Epic findEpicById(Integer id);

    void deleteAllEpics();

    void deleteAllSubtasks();

    Epic deleteEpic(Integer id);

    SubTask createSubTask(SubTask subTask, Epic epic);

    SubTask updateSubTask(SubTask subTask);

    List<SubTask> findAllSubTasks();

    SubTask findSubTaskById(Integer id);

    void deleteSubTaskById(Integer id);

    List<Integer> getSubTasksByEpicById(Integer epicId);
}
