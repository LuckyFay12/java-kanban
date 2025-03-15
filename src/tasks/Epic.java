package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class Epic extends Task {
    private List<Integer> subTasksId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW, Duration.ofMinutes(0), null);
    }

    public Epic(Integer id, String name, TaskStatus status, String description, Duration duration, LocalDateTime startTime) {
        super(id, name, TaskStatus.NEW, description, duration, startTime);
    }

    public Epic(Integer id, String name, TaskStatus status, String description, Duration duration, LocalDateTime startTime, LocalDateTime endTime) {
        super(id, name, TaskStatus.NEW, description, duration, startTime);
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return "Epic{" +
                "id=" + getId() +
                ", name=" + getName() +
                ", subTasksId=" + subTasksId +
                ", description=" + getDescription() +
                ", status=" + getStatus() +
                ", taskType=" + getTaskType() +
                ", duration=" + duration.toMinutes() +
                ", startTime=" + (startTime != null ? startTime.format(formatter) : "null") +
                ", endTime=" + (endTime != null ? endTime.format(formatter) : "null") +
                '}';
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    public List<Integer> getSubTasks() {
        return subTasksId;
    }

    public void addSubTask(SubTask subTask) {
        subTasksId.add(subTask.getId());
    }

    public void delSubTask(Integer subTaskID) {
        subTasksId.remove(subTaskID);
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}

















