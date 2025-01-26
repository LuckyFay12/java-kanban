package tasks;

import java.util.ArrayList;
import java.util.List;


public class Epic extends Task {
    private List<Integer> subTasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name=" + getName() +
                ", subTasksId=" + subTasksId +
                ", description=" + getDescription() +
                ", status=" + getStatus() +
                '}';
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
}
















