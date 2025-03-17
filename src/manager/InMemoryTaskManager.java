package manager;

import exceptions.CollisionTaskTimeException;
import exceptions.TaskNotFoundException;
import tasks.Task;
import tasks.Epic;
import tasks.SubTask;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> taskMap = new HashMap<>();
    protected final Map<Integer, Epic> epicMap = new HashMap<>();
    protected final Map<Integer, SubTask> subTaskMap = new HashMap<>();
    protected int counter = 0;
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    private HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private int nextId() {
        return counter++;
    }

    private boolean isStatus(List<SubTask> subtasks, TaskStatus status) {
        return subtasks.stream()
                .allMatch(subTask -> subTask.getStatus().equals(status));
    }

    private void refreshStatusEpic(Epic epic) {
        List<SubTask> epicSubTasks = getListSubTasksByEpic(epic.getId());
        if (epicSubTasks.isEmpty() || isStatus(epicSubTasks, TaskStatus.NEW)) {
            epic.setStatus(TaskStatus.NEW);
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ofMinutes(0));
        } else if (isStatus(epicSubTasks, TaskStatus.DONE)) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
        epic.setStartTime(
                epicSubTasks.stream()
                        .map(SubTask::getStartTime)
                        .filter(Objects::nonNull)
                        .min(LocalDateTime::compareTo)
                        .orElse(null)
        );
        epic.setEndTime(
                epicSubTasks.stream()
                        .map(SubTask::getEndTime)
                        .filter(Objects::nonNull)
                        .max(LocalDateTime::compareTo)
                        .orElse(null)  //Возвращаем null, если подзадач нет
        );
        epic.setDuration(
                epicSubTasks.stream()
                        .map(SubTask::getDuration)
                        .filter(Objects::nonNull)
                        .reduce(Duration.ZERO, Duration::plus)
        );
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void checkHasInteractions(Task newTask) throws CollisionTaskTimeException {
        List<Task> prioritizedTasks = getPrioritizedTasks();
        boolean hasCollision = prioritizedTasks.stream()
                .filter(t -> t.getStartTime() != null && t.getEndTime() != null)
                .anyMatch(t -> newTask.getStartTime().isBefore(t.getEndTime()) && t.getStartTime().isBefore(newTask.getEndTime()));
        if (hasCollision) {
            throw new CollisionTaskTimeException("Данная задача пересекается по времени с другой задачей");
        }
    }

    @Override
    public Task createTask(Task task) {
        checkHasInteractions(task);
        prioritizedTasks.add(task);
        int newId = nextId();
        task.setId(newId);
        taskMap.put(task.getId(), task);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        if (taskMap.containsKey(task.getId())) {
            prioritizedTasks.remove(task);
            Task availableTask = taskMap.get(task.getId());
            availableTask.setName(task.getName());
            availableTask.setDescription(task.getDescription());
            availableTask.setStatus(task.getStatus());
            availableTask.setDuration(task.getDuration());
            availableTask.setStartTime(task.getStartTime());
            checkHasInteractions(availableTask);
            prioritizedTasks.add(availableTask);
            return availableTask;
        }
        return null;
    }

    @Override
    public Task deleteTask(Integer id) {
        Task task = taskMap.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            historyManager.remove(id);
            return task;
        } else {
            throw new TaskNotFoundException("Задача c id " + id + " не найдена");
        }
    }

    @Override
    public List<Task> findAllTasks() {
        return taskMap.values().stream().toList();
    }

    @Override
    public Task findTaskById(Integer id) {
        Task task = taskMap.get(id);
        if (task != null) {
            historyManager.addToHistory(task);
        } else {
            throw new TaskNotFoundException("Задача c id " + id + " не найдена");
        }
        return task;
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    @Override
    public void deleteAllTasks() {
        taskMap.values().forEach(prioritizedTasks::remove);
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
            historyManager.addToHistory(epic);
        } else {
            throw new TaskNotFoundException("Эпик c id " + id + " не найден");
        }
        return epic;
    }

    @Override
    public void deleteAllEpics() {
        subTaskMap.values().forEach(prioritizedTasks::remove);
        subTaskMap.clear();
        epicMap.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subTaskMap.values().forEach(prioritizedTasks::remove);
        subTaskMap.clear();
        epicMap.values().forEach(epic -> {
            epic.getSubTasks().clear(); // Очищаем список подзадач у каждого эпика
            refreshStatusEpic(epic);
        });
    }

    @Override
    public Epic deleteEpic(Integer id) {
        Epic epic = epicMap.get(id);
        if (epic != null) {
            historyManager.remove(id);
            List<Integer> subTaskToDel = epic.getSubTasks();
            subTaskToDel.forEach(subTaskId -> {
                prioritizedTasks.remove(subTaskMap.get(subTaskId));
                subTaskMap.remove(subTaskId);
                historyManager.remove(subTaskId);
            });
            epicMap.remove(id);
            return epic;
        } else {
            throw new TaskNotFoundException("Эпик c id " + id + " не найден");
        }
    }

    @Override
    public SubTask createSubTask(SubTask subTask, Epic epic) {
        checkHasInteractions(subTask);
        prioritizedTasks.add(subTask);
        int newId = nextId();
        epic.getSubTasks().add(newId);
        subTask.setId(newId);
        subTaskMap.put(subTask.getId(), subTask);
        refreshStatusEpic(epic);
        return subTask;
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        if (subTaskMap.containsKey(subTask.getId())) {
            prioritizedTasks.remove(subTask);
            SubTask availableSubTask = subTaskMap.get(subTask.getId());
            availableSubTask.setName(subTask.getName());
            availableSubTask.setDescription(subTask.getDescription());
            availableSubTask.setStatus(subTask.getStatus());
            availableSubTask.setDuration(subTask.getDuration());
            availableSubTask.setStartTime(subTask.getStartTime());
            Epic epic = epicMap.get(subTask.getEpicId());
            refreshStatusEpic(epic);
            checkHasInteractions(availableSubTask);
            prioritizedTasks.add(availableSubTask);
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
        if (subTask != null) {
            historyManager.addToHistory(subTask);
        } else {
            throw new TaskNotFoundException("Подзадача c id " + id + " не найдена");
        }
        return subTask;
    }

    @Override
    public SubTask deleteSubTaskById(Integer id) {
        SubTask subtask = subTaskMap.get(id);
        prioritizedTasks.remove(subtask);
        historyManager.remove(id);
        if (subtask != null) {
            int epicId = subtask.getEpicId();
            int subTaskId = subtask.getId();
            subTaskMap.remove(id);
            Epic epicById = findEpicById(epicId);
            epicById.delSubTask(subTaskId);
            refreshStatusEpic(epicById);
            return subtask;
        } else {
            throw new TaskNotFoundException("Подзадача c id " + id + " не найдена");
        }
    }

    @Override
    public List<SubTask> getListSubTasksByEpic(Integer epicId) {
        Epic epic = epicMap.get(epicId);
        if (epic != null) {
            List<Integer> subTasksIds = epic.getSubTasks();
            List<SubTask> subTasks = subTasksIds.stream()
                    .map(id -> subTaskMap.get(id))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            return subTasks;
        } else {
            throw new TaskNotFoundException("Эпик c id " + epicId + " не найден");
        }
    }
}
