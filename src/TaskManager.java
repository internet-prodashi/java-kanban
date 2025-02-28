import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int newId = 1;

    // Все методы для задач
    public void addTask(Task task) {
        Task newTask = new Task(generateId(), task.getTitle(), task.getDescription(), task.getStatus());
        this.tasks.put(newTask.getId(), newTask);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task getTaskByID(int id) {
        return tasks.get(id);
    }

    public void deleteTaskByID(int id) {
        if (id >= 1) {
            if (tasks.containsKey(id)) {
                tasks.remove(id);
            }
        }
    }

    public void deleteAllTask() {
        tasks.clear();
    }

    public void updateTaskByID(Task task) {
        if (task.getId() >= 1) {
            if (tasks.containsKey(task.getId())) {
                tasks.put(task.getId(), task);
            }
        }
    }

    // Все методы для эпиков
    public void addEpic(Epic epic) {
        Epic newEpic = new Epic(generateId(), epic.getTitle(), epic.getDescription());
        this.epics.put(newEpic.getId(), newEpic);
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public Epic getEpicByID(int id) {
        Epic epic = null;
        if (id >= 1 && epics.containsKey(id)) {
            epic = epics.get(id);
        }
        return epic;
    }

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

    public void updateEpicByID(Epic epic) {
        if (epic.getId() >= 1 && epics.containsKey(epic.getId())) {
            Epic oldTask = epics.get(epic.getId());
            oldTask.setTitle(epic.getTitle());
            oldTask.setDescription(epic.getDescription());
        }
    }

    public void deleteAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    // Все методы для подзадач
    public void addSubtask(int epicId, Subtask subtask) {
        if (epicId >= 1 && epics.containsKey(epicId)) {
            Subtask newSubtask = new Subtask(generateId(), subtask.getTitle(), subtask.getDescription(), epicId);
            this.subtasks.put(newSubtask.getId(), newSubtask);
            epics.get(epicId).getSubtaskIdList().add(newSubtask.getId());
            updateStatusEpic(epicId);
        }
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public Subtask getSubtaskByID(int id) {
        Subtask subtask = null;
        if (id >= 1 && subtasks.containsKey(id)) {
            subtask = subtasks.get(id);
        }
        return subtask;
    }

    public void deleteSubtaskByID(int id) {
        if (id >= 1 && subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            subtasks.remove(id);
            deleteSubtaskIdEpic(subtask.getId(), subtask.getEpicId());
        }
    }

    public void updateSubtaskByID(Subtask subtask) {
        if (subtask.getId() >= 1 && subtask.getEpicId() >= 1 && subtasks.containsKey(subtask.getId()) && epics.containsKey(subtask.getEpicId())) {
            subtasks.put(subtask.getId(), subtask);
            updateStatusEpic(subtask.getEpicId());
        }
    }

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

    public void deleteAllSubtasks() {
        subtasks.clear();
        List<Integer> oldSubtaskId = null;
        for (Epic epic : epics.values()) {
            epic.setSubtaskIdList(oldSubtaskId);
            epic.setStatus(Status.NEW);
        }
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
            } else if (!statusList.contains(Status.NEW) && !statusList.contains(Status.IN_PROGRESS) && !statusList.contains(Status.DONE)) {
                oldEpic.setStatus(Status.DONE);
            } else {
                oldEpic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

}