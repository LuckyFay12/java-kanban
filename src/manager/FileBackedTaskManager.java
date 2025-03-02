package manager;

import exceptions.ManagerFileInitializationException;
import exceptions.ManagerSaveException;
import tasks.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File data;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.data = file;
    }

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        if (file == null) {
            throw new ManagerFileInitializationException("Файла не существует.");
        }

        try {
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), file);
            List<String> allLines = Files.readAllLines(file.toPath());
            int maxId = 0;
            for (String line : allLines) {
                Task task = fromString(line);
                if (task != null) {
                    if (task.getId() > maxId) {
                        maxId = task.getId(); // Обновляем максимальный ID
                    }
                    if (task.getTaskType() == TaskType.EPIC) {
                        fileBackedTaskManager.epicMap.put(task.getId(), (Epic) task);
                    } else if (task.getTaskType() == TaskType.SUBTASK) {
                        fileBackedTaskManager.subTaskMap.put(task.getId(), (SubTask) task);
                    } else {
                        fileBackedTaskManager.taskMap.put(task.getId(), task);
                    }
                }
            }
            fileBackedTaskManager.counter = maxId + 1;
            return fileBackedTaskManager;
        } catch (IOException e) {
            String errorMessage = "Ошибка загрузки задач из файла: " + e.getMessage();
            System.out.println(errorMessage);
            throw new ManagerFileInitializationException(errorMessage);
        }
    }

    private static Task fromString(String line) {
        String[] str = line.split(",");
        int id = Integer.parseInt(str[0]);
        TaskType taskType = TaskType.valueOf(str[1]);
        String name = str[2];
        TaskStatus taskStatus = TaskStatus.valueOf(str[3]);
        String description = str[4];
        Duration duration = str[5].isEmpty() ? null : Duration.ofMinutes(Long.parseLong(str[5]));
        LocalDateTime startTime = str[6].isEmpty() ? null : LocalDateTime.parse(str[6], Task.formatter);
        switch (taskType) {
            case TASK:
                return new Task(id, name, taskStatus, description, duration, startTime);
            case EPIC:
                return new Epic(id, name, taskStatus, description, duration, startTime);
            case SUBTASK:
                int idEpic = Integer.parseInt(str[7]);
                return new SubTask(id, name, taskStatus, description, duration, startTime, idEpic);
            default:
                return null;
        }
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);
        save();
        return createdEpic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask, Epic epic) {
        subTask.setEpicId(epic.getId());
        SubTask createdSubTask = super.createSubTask(subTask, epic);
        save();
        return createdSubTask;
    }

    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save();
        return createdTask;
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Epic deleteEpic(Integer id) {
        Epic deletedEpic = super.deleteEpic(id);
        save();
        return deletedEpic;
    }

    @Override
    public void deleteSubTaskById(Integer id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public Task deleteTask(Integer id) {
        Task deletedTask = super.deleteTask(id);
        save();
        return deletedTask;
    }

    @Override
    public List<Epic> findAllEpics() {
        return super.findAllEpics();
    }

    @Override
    public List<SubTask> findAllSubTasks() {
        return super.findAllSubTasks();
    }

    @Override
    public List<Task> findAllTasks() {
        return super.findAllTasks();
    }

    @Override
    public Epic findEpicById(Integer id) {
        Epic epic = super.findEpicById(id);
        return epic;
    }

    @Override
    public SubTask findSubTaskById(Integer id) {
        SubTask subTask = super.findSubTaskById(id);
        return subTask;
    }

    @Override
    public Task findTaskById(Integer id) {
        Task task = super.findTaskById(id);
        return task;
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        save();
        return updatedEpic;
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        SubTask updatedSubTask = super.updateSubTask(subTask);
        save();
        return updatedSubTask;
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        save();
        return updatedTask;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(data))) {
            for (Task task : taskMap.values()) {
                writer.write(taskToString(task) + "\n");
            }
            for (Epic epic : epicMap.values()) {
                writer.write(taskToString(epic) + "\n");
            }
            for (SubTask subTask : subTaskMap.values()) {
                writer.write(taskToString(subTask) + "\n");
            }
        } catch (IOException e) {
            String errorMessage = "Ошибка при сохранении в файл: " + e.getMessage();
            System.out.println(errorMessage);
            throw new ManagerSaveException(errorMessage);
        }
    }

    private String taskToString(Task task) {
        String str;
        String durationStr = task.getDuration() != null ? task.getDuration().toString() : "";
        String startTimeStr = task.getStartTime() != null ? task.getStartTime().format(Task.formatter) : "";
        if (task.getTaskType() == TaskType.SUBTASK) {
            str = String.join(",",
                    Integer.toString(task.getId()),
                    task.getTaskType().toString(),
                    task.getName(),
                    task.getStatus().toString(),
                    task.getDescription(),
                    durationStr,
                    startTimeStr,
                    Integer.toString(((SubTask) task).getEpicId())
            );
        } else {
            str = String.join(",",
                    Integer.toString(task.getId()),
                    task.getTaskType().toString(),
                    task.getName(),
                    task.getStatus().toString(),
                    task.getDescription(),
                    durationStr,
                    startTimeStr
            );
        }
        return str;
    }
}
