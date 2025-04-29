import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    // Все методы для задач
    void addTask(Task task);

    ArrayList<Task> getAllTasks();

    Task getTaskByID(int id);

    void deleteTaskByID(int id);

    void deleteAllTask();

    void updateTaskByID(Task task);

    // Все методы для эпиков
    void addEpic(Epic epic);

    ArrayList<Epic> getAllEpics();

    Epic getEpicByID(int id);

    void deleteEpicByID(int id);

    void updateEpicByID(Epic epic);

    void deleteAllEpic();

    // Все методы для подзадач
    void addSubtask(int epicId, Subtask subtask);

    ArrayList<Subtask> getAllSubtasks();

    Subtask getSubtaskByID(int id);

    void deleteSubtaskByID(int id);

    void updateSubtaskByID(Subtask subtask);

    List<Subtask> getSubtaskListByEpicId(int epicId);

    void deleteAllSubtasks();

    // История задач
    List<Task> getHistory();

    List<List<Task>> getAllTasksAllType();

    List<Task> getPrioritizedTasks();

}
