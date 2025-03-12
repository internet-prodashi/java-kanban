import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private final HistoryManager historyManager;
    private int newId = 1;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    // Все методы для задач
    @Override
    public void addTask(Task task) {
        Task newTask = new Task(generateId(), task.getTitle(), task.getDescription(), task.getStatus());
        this.tasks.put(newTask.getId(), newTask);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTaskByID(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public void deleteTaskByID(int id) {
        if (id >= 1) {
            if (tasks.containsKey(id)) {
                tasks.remove(id);
            }
        }
    }

    @Override
    public void deleteAllTask() {
        tasks.clear();
    }

    @Override
    public void updateTaskByID(Task task) {
        if (task.getId() >= 1) {
            if (tasks.containsKey(task.getId())) {
                tasks.put(task.getId(), task);
            }
        }
    }

    // Все методы для эпиков
    @Override
    public void addEpic(Epic epic) {
        Epic newEpic = new Epic(generateId(), epic.getTitle(), epic.getDescription(), Status.NEW);
        this.epics.put(newEpic.getId(), newEpic);
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic epic = null;
        if (id >= 1 && epics.containsKey(id)) {
            epic = epics.get(id);
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void deleteEpicByID(int id) {
        if (id >= 1 && epics.containsKey(id)) {
            Epic oldEpic = epics.get(id);
            List<Integer> oldSubtaskId = oldEpic.getSubtaskIdList();
            for (Integer subtaskId : oldSubtaskId) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    @Override
    public void updateEpicByID(Epic epic) {
        if (epic.getId() >= 1 && epics.containsKey(epic.getId())) {
            Epic oldTask = epics.get(epic.getId());
            oldTask.setTitle(epic.getTitle());
            oldTask.setDescription(epic.getDescription());
        }
    }

    @Override
    public void deleteAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    // Все методы для подзадач
    @Override
    public void addSubtask(int epicId, Subtask subtask) {
        if (epicId >= 1 && epics.containsKey(epicId)) {
            Subtask newSubtask = new Subtask(generateId(), subtask.getTitle(), subtask.getDescription(), subtask.getStatus(), epicId);
            this.subtasks.put(newSubtask.getId(), newSubtask);
            epics.get(epicId)
                    .getSubtaskIdList()
                    .add(newSubtask.getId());
            updateStatusEpic(epicId);
        }
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        Subtask subtask = null;
        if (id >= 1 && subtasks.containsKey(id)) {
            subtask = subtasks.get(id);
        }
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void deleteSubtaskByID(int id) {
        if (id >= 1 && subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            subtasks.remove(id);
            deleteSubtaskIdEpic(subtask.getId(), subtask.getEpicId());
        }
    }

    @Override
    public void updateSubtaskByID(Subtask subtask) {
        if (subtask.getId() >= 1 && subtask.getEpicId() >= 1 && subtasks.containsKey(subtask.getId()) && epics.containsKey(subtask.getEpicId())) {
            subtasks.put(subtask.getId(), subtask);
            updateStatusEpic(subtask.getEpicId());
        }
    }

    @Override
    public ArrayList<Subtask> getSubtaskListByEpicId(int epicId) {
        ArrayList<Subtask> newSubtask = new ArrayList<>();
        if (epicId >= 1 && epics.containsKey(epicId)) {
            Epic oldEpic = epics.get(epicId);
            List<Integer> oldSubtaskId = oldEpic.getSubtaskIdList();
            for (Integer subtaskId : oldSubtaskId) {
                newSubtask.add(subtasks.get(subtaskId));
            }
        }
        return newSubtask;
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        List<Integer> oldSubtaskId = null;
        for (Epic epic : epics.values()) {
            epic.setSubtaskIdList(oldSubtaskId);
            epic.setStatus(Status.NEW);
        }
    }

    // История задач
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Приватные методы вынес вниз класса
    private int generateId() {
        return newId++;
    }

    private void deleteSubtaskIdEpic(int subtaskId, int epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> oldSubtaskId = epic.getSubtaskIdList();
        oldSubtaskId.remove((Integer) subtaskId);
        updateStatusEpic(epicId);
    }

    private void updateStatusEpic(int epicId) {
        if (epicId >= 1 && epics.containsKey(epicId)) {
            Epic oldEpic = epics.get(epicId);
            List<Status> statusList = new ArrayList<>();
            List<Integer> oldSubtaskId = oldEpic.getSubtaskIdList();

            for (Integer subtaskId : oldSubtaskId) {
                Subtask oldSubtask = subtasks.get(subtaskId);
                statusList.add(oldSubtask.getStatus());
            }

            if (statusList.isEmpty() || (statusList.contains(Status.NEW) && !statusList.contains(Status.IN_PROGRESS) && !statusList.contains(Status.DONE))) {
                oldEpic.setStatus(Status.NEW);
            } else if (!statusList.contains(Status.NEW) && !statusList.contains(Status.IN_PROGRESS) && statusList.contains(Status.DONE)) {
                oldEpic.setStatus(Status.DONE);
            } else {
                oldEpic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

}