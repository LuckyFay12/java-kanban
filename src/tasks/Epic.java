package tasks;

import java.util.ArrayList;
import java.util.List;


public class Epic extends Task {
    private List<Integer> SubTasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name=" + getName() +
                ", SubTasksId=" + SubTasksId +
                ", description=" + getDescription() +
                ", status=" + getStatus() +
                '}';
    }

    public List<Integer> getSubtasks() {
        return SubTasksId;
    }

    public void setSubtasks(List<Integer> SubTasksId) {
        this.SubTasksId = SubTasksId;
    }

    public void addSubTask(SubTask subTask) {
        SubTasksId.add(subTask.getId());
    }

    public void delSubTask(Integer subTaskID) {
        SubTasksId.remove(subTaskID);
    }
}
















