package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime, int epicId) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }

    public SubTask(int id, TaskType taskType, String name, TaskStatus status, String description, Duration duration, LocalDateTime startTime, int epicId) {
        super(id, TaskType.SUBTASK, name, status, description, duration, startTime);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + getId() +
                ", name=" + getName() +
                ", description=" + getDescription() +
                ", status=" + getStatus() +
                ", taskType=" + taskType +
                ", duration=" + duration.toMinutes() +
                ", startTime=" + startTime.format(formatter) +
                ", endTime=" + getEndTime().format(formatter) +
                ", epicId=" + epicId +
                '}';
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }
}

