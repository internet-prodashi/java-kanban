import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> history;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        this.history = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (task != null && task.getId() > 0) {
            remove(task.getId());
            Node node = new Node(null, task, null);

            if (head == null) {
                head = node;
            } else {
                node.prev = tail;
                tail.next = node;
            }
            tail = node;

            history.put(task.getId(), node);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>();
        Node current = head;
        while (current != null) {
            tasks.add(current.element);
            current = current.next;
        }
        return tasks;
    }

    @Override
    public void remove(int id) {
        Node node = history.remove(id);
        if (node != null) {
            Node prevNode = node.prev;
            Node nextNode = node.next;
            if (head == node) {
                head = nextNode;
            }
            if (tail == node) {
                tail = prevNode;
            }
            if (prevNode != null) {
                prevNode.next = nextNode;
            }
            if (nextNode != null) {
                nextNode.prev = prevNode;
            }
        }
    }

    private static class Node {
        private final Task element;
        private Node next;
        private Node prev;

        Node(Node prev, Task element, Node next) {
            this.element = element;
            this.next = next;
            this.prev = prev;
        }
    }

}