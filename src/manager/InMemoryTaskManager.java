package manager;

import tasks.Task;
import tasks.Epic;
import tasks.SubTask;
import tasks.TaskStatus;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> taskMap = new HashMap<>();
    private HashMap<Integer, Epic> epicMap = new HashMap<>();
    private HashMap<Integer, SubTask> subTaskMap = new HashMap<>();
    private int counter = 0;

    private HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager){
        this.historyManager = historyManager;
    }

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

    @Override
    public Task createTask(Task task) {
        int newId = nextId();
        task.setId(newId);
        taskMap.put(task.getId(), task);
        return task;
    }

    @Override
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

    @Override
    public Task deleteTask(Integer id) {
        return taskMap.remove(id);
    }

    @Override
    public List<Task> findAllTasks() {
        return taskMap.values().stream().toList();
    }

    @Override
    public Task findTaskById(Integer id) {
        Task task = taskMap.get(id);
        if (task != null) {
            Task taskForHistory = new Task(task.getName(), task.getDescription(), task.getStatus());
            historyManager.addToHistory(taskForHistory);
        }
        return task;
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    @Override
    public void deleteAllTasks() {
        taskMap.clear();
    }

    @Override
    public Epic createEpic(Epic epic) {
        int newId = nextId();
        epic.setId(newId);
        epicMap.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epicMap.containsKey(epic.getId())) {
            Epic availableEpic = epicMap.get(epic.getId());
            availableEpic.setName(epic.getName());
            availableEpic.setDescription(epic.getDescription());
            return availableEpic;
        }
        return null;
    }

    @Override
    public List<Epic> findAllEpics() {
        return epicMap.values().stream().toList();
    }

    @Override
    public Epic findEpicById(Integer id) {
        Epic epic = epicMap.get(id);
        if (epic != null) {
                Epic epicForHistory = new Epic(epic.getName(), epic.getDescription(), epic.getStatus());
                historyManager.addToHistory(epicForHistory);
        }
        return epic;
    }

    @Override
    public void deleteAllEpics() {
        subTaskMap.clear();
        epicMap.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subTaskMap.clear();
        for (Epic epic : epicMap.values()) {
            epic.getSubtasks().clear();
            refreshStatusEpic(epic);
        }
    }

    @Override
    public Epic deleteEpic(Integer id) {
        Epic epic = epicMap.get(id);
        List<Integer> subTaskToDel = epic.getSubtasks();
        for (Integer i : subTaskToDel) {
            subTaskMap.remove(i);
        }
        return epicMap.remove(id);
    }

    @Override
    public SubTask createSubTask(SubTask subTask, Epic epic) {
        int newId = nextId();
        epic.getSubtasks().add(newId);
        subTask.setId(newId);
        subTaskMap.put(subTask.getId(), subTask);
        refreshStatusEpic(epic);
        return subTask;
    }

    @Override
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

    @Override
    public List<SubTask> findAllSubTasks() {
        return subTaskMap.values().stream().toList();
    }

    @Override
    public SubTask findSubTaskById(Integer id) {
        SubTask subTask = subTaskMap.get(id);
        if(subTask != null) {
            SubTask subTaskForHistory = new SubTask(subTask.getName(), subTask.getDescription(), subTask.getStatus(), subTask.getEpicId());
            historyManager.addToHistory(subTaskForHistory);
        }
        return subTask;
    }

    @Override
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

    @Override
    public List<Integer> getSubTasksByEpicById(Integer epicId) {
        if (epicMap.containsKey(epicId)) {
            Epic epic = epicMap.get(epicId);
            List<Integer> subTasks = epic.getSubtasks();
            return subTasks;
        }
        return null;
    }
}
