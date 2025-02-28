import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int newId = 1;

    public int generateId() {
        return newId++;
    }

    // Все методы для задач
    public void addTask(Task task) {
        int taskId = generateId();
        Task newTask = new Task(taskId, task.getTitle(), task.getDescription());
        this.tasks.put(taskId, newTask);
    }

    public ArrayList<Task> printTask() {
        ArrayList<Task> newTask = new ArrayList<>();
        for (Task task : tasks.values()) {
            newTask.add(task);
        }
        return newTask;
    }

    public Task getTaskByID(int id) {
        Task task = null;
        if (id >= 1) {
            if (tasks.containsKey(id)) {
                task = tasks.get(id);
            }
        }
        return task;
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
                tasks.remove(task.getId());
                tasks.put(task.getId(), task);
            }
        }
    }

    // Все методы для эпиков
    public void addEpic(Epic epic) {
        int taskId = generateId();
        Epic newEpic = new Epic(taskId, epic.getTitle(), epic.getDescription());
        this.epics.put(taskId, newEpic);
    }

    public ArrayList<Epic> printEpic() {
        ArrayList<Epic> newEpic = new ArrayList<>();
        for (Epic epic : epics.values()) {
            newEpic.add(epic);
        }
        return newEpic;
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
            List<Integer> oldSubtaskId = oldEpic.getSubtaskId();
            for (Integer subtaskId : oldSubtaskId) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    public void updateEpicByID(Epic epic) {
        if (epic.getId() >= 1 && epics.containsKey(epic.getId())) {
            Epic oldTask = epics.get(epic.getId());
            epics.remove(epic.getId());
            epic.setStatus(oldTask.getStatus());
            epic.setSubtaskId(oldTask.getSubtaskId());
            epics.put(epic.getId(), epic);
        }
    }

    public void deleteAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    public void updateSubtaskEpic(int subtaskId, int epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> listSubtaskId = epic.getSubtaskId();
        listSubtaskId.add(subtaskId);
        updateStatusEpic(epicId);
    }

    public void deleteSubtaskIdEpic(int subtaskId, int epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> oldSubtaskId = epic.getSubtaskId();
        oldSubtaskId.remove((Integer) subtaskId);
        updateStatusEpic(epicId);
    }

    // Все методы для подзадач
    public void addSubtask(int epicId, Subtask subtask) {
        if (epicId >= 1 && epics.containsKey(epicId)) {
            int taskId = generateId();
            Subtask newSubtask = new Subtask(taskId, subtask.getTitle(), subtask.getDescription(), epicId);
            this.subtasks.put(taskId, newSubtask);
            updateSubtaskEpic(taskId, epicId);
        }
    }

    public ArrayList<Subtask> printSubtask() {
        ArrayList<Subtask> newSubtask = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            newSubtask.add(subtask);
        }
        return newSubtask;
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
            subtasks.remove(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
            updateStatusEpic(subtask.getEpicId());
        }
    }

    public void updateStatusEpic(int epicId) {
        if (epicId >= 1 && epics.containsKey(epicId)) {
            Epic oldEpic = epics.get(epicId);
            List<Status> listStatus = new ArrayList<>();
            List<Integer> oldSubtaskId = oldEpic.getSubtaskId();

            for (Integer subtaskId : oldSubtaskId) {
                Subtask oldSubtask = subtasks.get(subtaskId);
                listStatus.add(oldSubtask.getStatus());
            }

            if (listStatus.isEmpty() || (listStatus.contains(Status.NEW) && !listStatus.contains(Status.IN_PROGRESS) && !listStatus.contains(Status.DONE))) {
                oldEpic.setStatus(Status.NEW);
            } else if (!listStatus.contains(Status.NEW) && !listStatus.contains(Status.IN_PROGRESS) && !listStatus.contains(Status.DONE)) {
                oldEpic.setStatus(Status.DONE);
            } else {
                oldEpic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    public ArrayList<Subtask> getListSubtaskByEpic(int epicId) {
        ArrayList<Subtask> newSubtask = new ArrayList<>();
        if (epicId >= 1 && epics.containsKey(epicId)) {
            Epic oldEpic = epics.get(epicId);
            List<Integer> oldSubtaskId = oldEpic.getSubtaskId();
            for (Integer subtaskId : oldSubtaskId) {
                newSubtask.add(subtasks.get(subtaskId));
            }
        }
        return newSubtask;
    }

    public void deleteAllSubtask() {
        subtasks.clear();
        List<Integer> oldSubtaskId = null;
        for (Epic epic : epics.values()) {
            epic.setSubtaskId(oldSubtaskId);
            epic.setStatus(Status.NEW);
        }
    }

}