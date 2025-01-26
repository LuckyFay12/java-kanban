package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    Node first;
    Node last;
    Map<Integer, Node> idToNode = new HashMap<>();

    private static class Node {
        Node previous;
        Task value;
        Node next;

        public Node(Node previous, Task value, Node next) {
            this.previous = previous;
            this.value = value;
            this.next = next;
        }
    }

    private void removeNode(Node node) {
        //обновим first и last
        if (node == null) return;
        Node next = node.next;
        Node prev = node.previous;
        if (prev != null) {
            prev.next = next;
        } else {
            first = next;
        }
        if (next != null) {
            next.previous = prev;
        } else {
            last = prev;
        }
    }

    private void linkLast(Node node) {
        if (last != null) {
            Node oldLast = last;
            node.previous = oldLast;
            oldLast.next = node;
        } else {
            first = node;
        }
        last = node;
    }

    @Override
    public void addToHistory(Task task) {
        if (task == null) return;
        remove(task.getId());
        Node newNode = new Node(null, task, null);
        linkLast(newNode);
        idToNode.put(task.getId(), newNode);
       }


    @Override
    public void remove(int id) {
        removeNode(idToNode.remove(id));
    }

    @Override
    public List<Task> getHistory() {
        Node cursor = first;
        List<Task> tasksHistory = new ArrayList<>();
        while (cursor != null) {
            tasksHistory.add(cursor.value);
            cursor = cursor.next;
        }
        return tasksHistory;
    }
}
