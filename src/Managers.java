import java.io.File;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultFile(File file) {
        return new FileBackedTaskManager(file);
    }

    public static TaskManager getDefaultLoadFromFile(File file) {
        return FileBackedTaskManager.loadFromFile(file);
    }

}
