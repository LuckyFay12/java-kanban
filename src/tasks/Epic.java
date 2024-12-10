package tasks;
import java.util.ArrayList;
import java.util.List;


public class Epic extends Task {
    private List<Integer> SubTasksID = new ArrayList<>();

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name=" + getName() +
                ", SubTasksID=" + SubTasksID +
                ", description=" + getDescription() +
                ", status=" + getStatus() +
                '}';
    }

    public List<Integer> getSubtasks() {
        return SubTasksID;
    }

    public void setSubtasks(List<Integer> SubTasksID) {
        this.SubTasksID = SubTasksID;
    }

    public void addSubTask(SubTask subTask) {
        SubTasksID.add(subTask.getId());
    }
}
















