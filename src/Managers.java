public class Managers {

    private static TaskManager defaultTaskManager;
    private static HistoryManager defaultHistoryManager;

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
