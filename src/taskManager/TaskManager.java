package taskManager;

import tasks.Task;
import tasks.Epic;
import tasks.SubTask;
import tasks.TaskStatus;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class TaskManager {
    private HashMap<Integer, Task> taskMap = new HashMap<>();
    private HashMap<Integer, Epic> epicMap = new HashMap<>();
    private HashMap<Integer, SubTask> subTaskMap = new HashMap<>();
    private int counter = 0;

    private int nextId() {
        return counter++;
    }

    private boolean isStatus(List<SubTask> subtasks, TaskStatus status) {
        for (SubTask sbTask : subtasks) {
            if (!sbTask.getStatus().equals(status)) {
                return false;
            }
        }
        return true;
    }

    private void refreshStatusEpic(Epic epic) {
        List<SubTask> epicSubTasks = getListSubTasksByEpic(epic.getSubtasks());
        if (epicSubTasks.isEmpty() || isStatus(epicSubTasks, TaskStatus.NEW)) {
            epic.setStatus(TaskStatus.NEW);
        } else if (isStatus(epicSubTasks, TaskStatus.DONE)) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private List<SubTask> getListSubTasksByEpic(List<Integer> subTaskIds) {
        List<SubTask> subTasks = new ArrayList<>();
        for (Integer id : subTaskIds) {
            subTasks.add(subTaskMap.get(id));
        }
        return subTasks;
    }

    public Task createTask(Task task) {
        int newId = nextId();
        task.setId(newId);
        taskMap.put(task.getId(), task);
        return task;
    }

    public Task updateTask(Task task) {
        if (taskMap.containsKey(task.getId())) {
            Task availableTask = taskMap.get(task.getId());
            availableTask.setName(task.getName());
            availableTask.setDescription(task.getDescription());
            availableTask.setStatus(task.getStatus());
            return availableTask;
        }
        return null;
    }

    public Task deleteTask(Integer id) {
        return taskMap.remove(id);
    }

    public List<Task> findAllTasks() {
        return taskMap.values().stream().toList();
    }

    public Task findTaskById(Integer id) {
        return taskMap.get(id);
    }

    public void deleteAllTasks() {
        taskMap.clear();
    }

    public Epic createEpic(Epic epic) {
        int newId = nextId();
        epic.setId(newId);
        epicMap.put(epic.getId(), epic);
        return epic;
    }

    public Epic updateEpic(Epic epic) {
        if (epicMap.containsKey(epic.getId())) {
            Epic availableEpic = epicMap.get(epic.getId());
            availableEpic.setName(epic.getName());
            availableEpic.setDescription(epic.getDescription());
            return availableEpic;
        }
        return null;
    }

    public List<Epic> findAllEpics() {
        return epicMap.values().stream().toList();
    }

    public Epic findEpicById(Integer id) {
        return epicMap.get(id);
    }

    public void deleteAllEpics() {
        subTaskMap.clear();
        epicMap.clear();
    }

    public void deleteAllSubtasks() {
        subTaskMap.clear();
        for (Epic epic : epicMap.values()) {
            epic.getSubtasks().clear();
            refreshStatusEpic(epic);
        }
    }

    public Epic deleteEpic(Integer id) {
        Epic epic = epicMap.get(id);
        List<Integer> subTaskToDel = epic.getSubtasks();
        for (Integer i : subTaskToDel) {
            subTaskMap.remove(i);
        }
        return epicMap.remove(id);
    }

    public SubTask createSubTask(SubTask subTask, Epic epic) {
        int newId = nextId();
        epic.getSubtasks().add(newId);
        subTask.setId(newId);
        subTaskMap.put(subTask.getId(), subTask);
        refreshStatusEpic(epic);
        return subTask;
    }

    public SubTask updateSubTask(SubTask subTask) {
        if (subTaskMap.containsKey(subTask.getId())) {
            SubTask availableSubTask = subTaskMap.get(subTask.getId());
            availableSubTask.setName(subTask.getName());
            availableSubTask.setDescription(subTask.getDescription());
            availableSubTask.setStatus(subTask.getStatus());
            Epic epic = epicMap.get(subTask.getEpicId());
            refreshStatusEpic(epic);
            return availableSubTask;
        }
        return null;
    }

    public List<SubTask> findAllSubTasks() {
        return subTaskMap.values().stream().toList();
    }

    public SubTask findSubTaskById(Integer id) {
        return subTaskMap.get(id);
    }

    public void deleteSubTaskById(Integer id) {

        SubTask subtask = subTaskMap.get(id);
        if (subtask != null) {
            int epicId = subtask.getEpicId();
            int subTaskId = subtask.getId();
            subTaskMap.remove(id);
            Epic epicById = findEpicById(epicId);
            epicById.delSubTask(subTaskId);
            refreshStatusEpic(epicById);
        }
    }

    public List<Integer> getSubTasksByEpicById(Integer epicId) {
        if (epicMap.containsKey(epicId)) {
            Epic epic = epicMap.get(epicId);
            List<Integer> subTasks = epic.getSubtasks();
            return subTasks;
        }
        return null;
    }
}
