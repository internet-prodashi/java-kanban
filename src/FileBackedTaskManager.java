import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    public FileBackedTaskManager(File file, HashMap<Integer, Task> tasks, HashMap<Integer, Epic> epics, HashMap<Integer, Subtask> subtasks, int newId) {
        super(tasks, epics, subtasks, newId);
        this.file = file;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void deleteTaskByID(int id) {
        super.deleteTaskByID(id);
        save();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void updateTaskByID(Task task) {
        super.updateTaskByID(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void deleteEpicByID(int id) {
        super.deleteEpicByID(id);
        save();
    }

    @Override
    public void updateEpicByID(Epic epic) {
        super.updateEpicByID(epic);
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void addSubtask(int epicId, Subtask subtask) {
        super.addSubtask(epicId, subtask);
        save();
    }

    @Override
    public void deleteSubtaskByID(int id) {
        super.deleteSubtaskByID(id);
        save();
    }

    @Override
    public void updateSubtaskByID(Subtask subtask) {
        super.updateSubtaskByID(subtask);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        List<Task> tasksDB = new ArrayList<>();

        try (BufferedReader fileTasksDB = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {

            String task = fileTasksDB.readLine();

            if (task != null && task.trim().equals("id,type,name,status,description,epic")) {
                task = fileTasksDB.readLine();
            } else {
                throw new ManagerSaveException("Первая строка в файле должна иметь вид: id,type,name,status,description,epic");
            }

            while (task != null && !task.isEmpty()) {
                String[] splitTask = task.split(",");
                if (splitTask[1].equals(TypeTask.TASK.toString())) {
                    tasksDB.add(new Task(Integer.parseInt(splitTask[0]), splitTask[2], splitTask[4], Status.valueOf(splitTask[3])));
                } else if (splitTask[1].equals(TypeTask.EPIC.toString())) {
                    List<Integer> subtaskIdList = new ArrayList<>();
                    if (splitTask.length > 5) {
                        for (int i = 5; i < splitTask.length; i++) {
                            subtaskIdList.add(Integer.parseInt(splitTask[i]));
                        }
                    }
                    tasksDB.add(new Epic(Integer.parseInt(splitTask[0]), splitTask[2], splitTask[4], Status.valueOf(splitTask[3]), subtaskIdList));
                } else if (splitTask[1].equals(TypeTask.SUBTASK.toString())) {
                    tasksDB.add(new Subtask(Integer.parseInt(splitTask[0]), splitTask[2], splitTask[4], Status.valueOf(splitTask[3]), Integer.parseInt(splitTask[5])));
                }
                task = fileTasksDB.readLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Файл не найден (не прочитан)", e);
        }

        HashMap<Integer, Task> tasks = new HashMap<>();
        HashMap<Integer, Epic> epics = new HashMap<>();
        HashMap<Integer, Subtask> subtasks = new HashMap<>();
        int newId = 0;
        for (Task task : tasksDB) {
            if (task instanceof Epic epic) {
                epics.put(epic.getId(), epic);
                if (epic.getId() > newId) {
                    newId = epic.getId();
                }
            } else if (task instanceof Subtask subtask) {
                subtasks.put(subtask.getId(), subtask);
                if (subtask.getId() > newId) {
                    newId = subtask.getId();
                }
            } else if (task instanceof Task taskNew) {
                tasks.put(taskNew.getId(), taskNew);
                if (taskNew.getId() > newId) {
                    newId = taskNew.getId();
                }
            }
        }
        newId = newId + 1;
        return new FileBackedTaskManager(file, tasks, epics, subtasks, newId);
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(
                file.toPath(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write("id,type,name,status,description,epic");

            for (List<Task> list : getAllTasksAllType()) {
                for (Task task : list) {
                    String serializedTask = taskToString(task);
                    writer.newLine();
                    writer.write(serializedTask);
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения задачи", e);
        }
    }

    private String taskToString(Task task) {
        String stringTask = "";

        if (task instanceof Epic epic) {
            stringTask += epic.getId() + ","
                    + TypeTask.EPIC + ","
                    + epic.getTitle() + ","
                    + epic.getStatus() + ","
                    + epic.getDescription();
            if (epic.getSubtaskIdList() != null) {
                stringTask += "," + epic.getSubtaskIdList()
                        .toString()
                        .replaceAll("\\[", "")
                        .replaceAll("]", "")
                        .replaceAll(" ", "");
            }
        } else if (task instanceof Subtask subtask) {
            stringTask += subtask.getId() + ","
                    + TypeTask.SUBTASK + ","
                    + subtask.getTitle() + ","
                    + subtask.getStatus() + ","
                    + subtask.getDescription() + ","
                    + subtask.getEpicId();
        } else if (task instanceof Task taskNew) {
            stringTask = taskNew.getId() + ","
                    + TypeTask.TASK + ","
                    + taskNew.getTitle() + ","
                    + taskNew.getStatus() + ","
                    + taskNew.getDescription();
        }

        return stringTask;
    }

}
