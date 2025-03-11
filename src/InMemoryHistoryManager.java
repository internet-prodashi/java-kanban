import java.util.ArrayList;
//import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int MAX_SIZE_HISTORY = 10;
    private final List<Task> history;

    public InMemoryHistoryManager() {
        // Если часто вставляем и удаляем, правильно делать так, но в теории практикума этого не было...
        //this.history = new LinkedList<>();
        this.history = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (history.size() == MAX_SIZE_HISTORY) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getDefaultHistory() {
        return List.copyOf(this.history);
    }

}
