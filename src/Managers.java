public class Managers {

    private TaskManager defaultTaskManager;
    private HistoryManager defaultHistoryManager;

    public Managers() {
        this.defaultHistoryManager = new InMemoryHistoryManager();
        this.defaultTaskManager = new InMemoryTaskManager(defaultHistoryManager);
    }

    public TaskManager getDefaultTaskManager() {
        return defaultTaskManager;
    }

}
