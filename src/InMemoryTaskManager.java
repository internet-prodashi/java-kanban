import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private final HistoryManager historyManager;
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    private int newId = 1;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    public InMemoryTaskManager(HashMap<Integer, Task> tasks, HashMap<Integer, Epic> epics, HashMap<Integer, Subtask> subtasks, int newId) {
        this.historyManager = Managers.getDefaultHistory();
        this.tasks = tasks;
        this.epics = epics;
        this.subtasks = subtasks;
        this.newId = newId;
    }

    // Все методы для задач
    @Override
    public void addTask(Task task) {
        Task newTask = new Task(generateId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getStartTime(), task.getDuration());
        this.tasks.put(newTask.getId(), newTask);
        if (newTask.getStartTime() != null && newTask.getEndTime() != null && !isOverlapTasksInTime(newTask)) {
            addTaskInPriorityList(newTask);
        }
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
                deleteTaskFromPriorityList(tasks.get(id));
                tasks.remove(id);
            }
        }
    }

    @Override
    public void deleteAllTask() {
        tasks.clear();
        deleteSpecificTypeOfTasksFromPriorityList(Task.class);
    }

    @Override
    public void updateTaskByID(Task task) {
        if (task.getId() >= 1) {
            if (tasks.containsKey(task.getId())) {
                if (task.getStartTime() != null && task.getEndTime() != null && !isOverlapTasksInTime(task)) {
                    updatePriorityList(tasks.get(task.getId()), task);
                }
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

            oldSubtaskId.stream().forEach(subtaskId -> {
                deleteTaskFromPriorityList(subtasks.get(subtaskId));
                subtasks.remove(subtaskId);
            });
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
        deleteSpecificTypeOfTasksFromPriorityList(Epic.class);
        deleteSpecificTypeOfTasksFromPriorityList(Subtask.class);
    }

    // Все методы для подзадач
    @Override
    public void addSubtask(int epicId, Subtask subtask) {
        if (epicId >= 1 && epics.containsKey(epicId)) {
            Subtask newSubtask = new Subtask(generateId(), subtask.getTitle(), subtask.getDescription(), subtask.getStatus(), epicId, subtask.getStartTime(), subtask.getDuration());
            this.subtasks.put(newSubtask.getId(), newSubtask);
            if (newSubtask.getStartTime() != null && newSubtask.getEndTime() != null && !isOverlapTasksInTime(newSubtask)) {
                addTaskInPriorityList(newSubtask);
            }
            epics.get(epicId).getSubtaskIdList().add(newSubtask.getId());
            updateStatusEpic(epicId);
            updateTimeExecutionEpic(epicId);
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
            deleteSubtaskIdEpic(subtask.getId(), subtask.getEpicId());
            deleteTaskFromPriorityList(subtask);
            subtasks.remove(id);
        }
    }

    @Override
    public void updateSubtaskByID(Subtask subtask) {
        if (subtask.getId() >= 1 && subtask.getEpicId() >= 1 && subtasks.containsKey(subtask.getId()) && epics.containsKey(subtask.getEpicId())) {
            if (subtask.getStartTime() != null && subtask.getEndTime() != null && !isOverlapTasksInTime(subtask)) {
                updatePriorityList(subtasks.get(subtask.getId()), subtask);
            }
            subtasks.put(subtask.getId(), subtask);
            updateStatusEpic(subtask.getEpicId());
            updateTimeExecutionEpic(subtask.getEpicId());
        }
    }

    @Override
    public List<Subtask> getSubtaskListByEpicId(int epicId) {
        List<Subtask> newSubtask = new ArrayList<>();
        if (epicId >= 1 && epics.containsKey(epicId)) {
            Epic oldEpic = epics.get(epicId);
            List<Integer> oldSubtaskId = oldEpic.getSubtaskIdList();
            newSubtask = oldSubtaskId.stream().map(subtasks::get).collect(Collectors.toList());
        }
        return newSubtask;
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        deleteSpecificTypeOfTasksFromPriorityList(Subtask.class);
        epics.values().stream().forEach(epic -> {
            epic.setSubtaskIdList(null);
            epic.setStatus(Status.NEW);
            updateTimeExecutionEpic(epic.getId());
        });
    }

    // Методы над всеми типами задач
    public List<List<Task>> getAllTasksAllType() {
        return List.of(new ArrayList<>(tasks.values()), new ArrayList<>(epics.values()), new ArrayList<>(subtasks.values()));
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return List.copyOf(prioritizedTasks);
    }

    // История задач
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Приватные методы вынес вниз класса
    private boolean isOverlapTasksInTime(Task task) {
        return prioritizedTasks.stream().anyMatch(priorityTask -> {
            if (priorityTask.getStartTime() != null && priorityTask.getEndTime() != null && task.getStartTime() != null && task.getEndTime() != null) {
                return priorityTask.getEndTime().isAfter(task.getStartTime()) && task.getEndTime().isAfter(priorityTask.getStartTime());
            } else {
                return false;
            }
        });
    }

    private void addTaskInPriorityList(Task task) {
        prioritizedTasks.add(task);
    }

    private void deleteTaskFromPriorityList(Task task) {
        prioritizedTasks.remove(task);
    }

    private void updatePriorityList(Task oldTask, Task newTask) {
        deleteTaskFromPriorityList(oldTask);
        addTaskInPriorityList(newTask);
    }

    private void deleteSpecificTypeOfTasksFromPriorityList(Class<? extends Task> classTask) {
        prioritizedTasks.removeIf(task -> task.getClass().equals(classTask));
    }

    private int generateId() {
        return newId++;
    }

    private void deleteSubtaskIdEpic(int subtaskId, int epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> oldSubtaskId = epic.getSubtaskIdList();
        oldSubtaskId.remove((Integer) subtaskId);
        updateStatusEpic(epicId);
        updateTimeExecutionEpic(epicId);
    }

    private void updateTimeExecutionEpic(int epicId) {
        if (epicId >= 1 && epics.containsKey(epicId)) {
            Epic oldEpic = epics.get(epicId);
            List<Integer> oldSubtaskId = oldEpic.getSubtaskIdList();
            if (oldSubtaskId != null) {
                List<Subtask> subtaskList = oldSubtaskId.stream().map(this::getSubtaskByID).toList();

                LocalDateTime startTime = subtaskList.stream().map(Subtask::getStartTime).filter(Objects::nonNull).min(Comparator.naturalOrder()).orElse(null);
                oldEpic.setStartTime(startTime);

                Duration durationAllSubtask = subtaskList.stream().map(Subtask::getDuration).filter(Objects::nonNull).reduce(Duration.ZERO, Duration::plus);
                oldEpic.setDuration(durationAllSubtask);

                LocalDateTime endTimeNew = subtaskList.stream().filter(s -> s.getStartTime() != null && s.getDuration() != null).map(Subtask::getEndTime).max(Comparator.naturalOrder()).orElse(null);
                oldEpic.setEndTime(endTimeNew);
            }
        }
    }

    private void updateStatusEpic(int epicId) {
        if (epicId >= 1 && epics.containsKey(epicId)) {
            Epic oldEpic = epics.get(epicId);
            List<Integer> oldSubtaskId = oldEpic.getSubtaskIdList();

            List<Status> statusList = oldSubtaskId.stream().map(subtaskId -> subtasks.get(subtaskId).getStatus()).toList();

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