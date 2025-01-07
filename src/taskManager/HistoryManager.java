package taskManager;
import tasks.Task;
import tasks.Epic;
import tasks.SubTask;
import java.util.List;

public interface HistoryManager {
   void addToHistory(Task task);

   List<Task> getHistory();
}
