package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Epic extends Task {
    private List<Integer> subTasksId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW, Duration.ofMinutes(0), null);
    }

    public Epic(int id, TaskType taskType, String name, TaskStatus status, String description, Duration duration, LocalDateTime startTime) {
        super(id, taskType, name, TaskStatus.NEW, description, duration, startTime);
    }

    public Epic(int id, TaskType taskType, String name, TaskStatus status, String description, Duration duration, LocalDateTime startTime, LocalDateTime endTime) {
        super(id, taskType, name, TaskStatus.NEW, description, duration, startTime);
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name=" + getName() +
                ", subTasksId=" + subTasksId +
                ", description=" + getDescription() +
                ", status=" + getStatus() +
                ", taskType=" + taskType +
                ", duration=" + duration.toMinutes() +
                ", startTime=" + startTime.format(formatter) +
                ", endTime=" + endTime.format(formatter) +
                '}';
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    public List<Integer> getSubtasks() {
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

















