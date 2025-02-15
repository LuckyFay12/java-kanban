package tasks;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String name, String description, TaskStatus status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public SubTask(int id, TaskType taskType, String name, TaskStatus status, String description, int epicId) {
        super(id, taskType, name, status, description);
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
                "epicId=" + epicId +
                ", name=" + getName() +
                ", description=" + getDescription() +
                ", status=" + getStatus() +
                '}';
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }
}

