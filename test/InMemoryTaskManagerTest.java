import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    TaskManager taskManager = Managers.getDefault();

    @BeforeEach
    void setUp() {
        Task oneTask = new Task("Задача 1", "Описание задачи 1");
        Task twoTask = new Task("Задача 2", "Описание задачи 2");
        Task threeTask = new Task("Задача 3", "Описание задачи 3");
        taskManager.addTask(oneTask);
        taskManager.addTask(twoTask);
        taskManager.addTask(threeTask);

        Epic oneEpic = new Epic("Эпик 1", "Описание эпика 1");
        Epic twoEpic = new Epic("Эпик 2", "Описание эпика 2");
        Epic threeEpic = new Epic("Эпик 3", "Описание эпика 3");
        taskManager.addEpic(oneEpic);
        taskManager.addEpic(twoEpic);
        taskManager.addEpic(threeEpic);

        Subtask oneSubtaskForOneEpic = new Subtask("Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1", Status.NEW, 4);
        Subtask twoSubtaskForOneEpic = new Subtask("Подзадача 2 эпика 1", "Описание подзадачи 2 эпика 1", Status.NEW, 4);
        Subtask threeSubtaskForOneEpic = new Subtask("Подзадача 3 эпика 1", "Описание подзадачи 3 эпика 1", Status.NEW, 4);
        Subtask oneSubtaskForTwoEpic = new Subtask("Подзадача 1 эпика 2", "Описание подзадачи 1 эпика 2", Status.IN_PROGRESS, 5);
        Subtask twoSubtaskForTwoEpic = new Subtask("Подзадача 2 эпика 2", "Описание подзадачи 2 эпика 2", Status.IN_PROGRESS, 5);
        Subtask oneSubtaskForThreeEpic = new Subtask("Подзадача 1 эпика 3", "Описание подзадачи 1 эпика 3", Status.DONE, 6);
        taskManager.addSubtask(4, oneSubtaskForOneEpic);
        taskManager.addSubtask(4, twoSubtaskForOneEpic);
        taskManager.addSubtask(4, threeSubtaskForOneEpic);
        taskManager.addSubtask(5, oneSubtaskForTwoEpic);
        taskManager.addSubtask(5, twoSubtaskForTwoEpic);
        taskManager.addSubtask(6, oneSubtaskForThreeEpic);
    }

    @Test
    void idTasksShouldBeEqual() {
        Epic oneEpic = new Epic(1, "Эпик 1", "Описание эпика 1");
        Epic twoEpic = new Epic(1, "Эпик 1", "Описание эпика 1");
        assertEquals(oneEpic, twoEpic, "Идентификаторы эпиков не равны!");

        Task oneTask = new Task(5, "Задача 1", "Описание задачи 1");
        Task twoTask = new Task(5, "Задача 2", "Описание задачи 2");
        assertEquals(oneTask, twoTask, "Идентификаторы задач не равны!");

        Subtask oneSubtaskForOneEpic = new Subtask(10, "Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1", 1);
        Subtask twoSubtaskForOneEpic = new Subtask(10, "Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1", 1);
        assertEquals(oneSubtaskForOneEpic, twoSubtaskForOneEpic, "Идентификаторы подзадач не равны!");
    }

    @Test
    void testHistory() {
        for (int i = 0; i < 5; i++) {
            Task task = new Task("Задача " + i, "Описание задачи " + i);
            taskManager.addTask(task);
        }

        List<Task> allTasks = taskManager.getAllTasks();
        for (Task task : allTasks) {
            taskManager.getTaskByID(task.getId());
        }
        List<Epic> allEpics = taskManager.getAllEpics();
        for (Epic epic : allEpics) {
            taskManager.getEpicByID(epic.getId());
        }
        List<Subtask> allSubtasks = taskManager.getAllSubtasks();
        for (Subtask subtask : allSubtasks) {
            taskManager.getSubtaskByID(subtask.getId());
        }

        List<Task> history = taskManager.getHistory();

        Task firstHistoryTask = taskManager.getTaskByID(16);
        Epic fourHistoryTask = taskManager.getEpicByID(6);
        Subtask lastHistoryTask = taskManager.getSubtaskByID(12);

        assertEquals(firstHistoryTask, history.getFirst());
        assertEquals(fourHistoryTask, history.get(history.size() - 7));
        assertEquals(lastHistoryTask, history.getLast());

        assertTrue(history.containsAll(allEpics), "Эпики не найдены в истории");
        assertTrue(history.containsAll(allSubtasks), "Подзадачи не найдены в истории");

        Subtask subtaskUpdate = new Subtask(12, "Подзадача 1 эпика 3 обновлена", "Описание подзадачи 1 эпика 3 обновлен0", Status.IN_PROGRESS, 6);
        taskManager.updateSubtaskByID(subtaskUpdate);

        assertNotEquals(lastHistoryTask.getTitle(), subtaskUpdate.getTitle(), "Неверное название подзадачи");
        assertNotEquals(lastHistoryTask.getDescription(), subtaskUpdate.getDescription(), "Неверное описание подзадачи");
    }


    @Test
    void testRemoveTask() {
        Task oneTask = new Task(1, "Задача 1", "Описание задачи 1");
        Task twoTask = new Task(2, "Задача 2", "Описание задачи 2");
        Task threeTask = new Task(3, "Задача 3", "Описание задачи 3");
        Task fourTask = new Task(4, "Задача 4", "Описание задачи 4");
        Task fiveTask = new Task(5, "Задача 5", "Описание задачи 5");

        InMemoryHistoryManager newHistory = new InMemoryHistoryManager();
        newHistory.add(oneTask);
        newHistory.add(twoTask);
        newHistory.add(threeTask);
        newHistory.add(fourTask);
        newHistory.add(fiveTask);

        newHistory.remove(2);

        List<Task> twoHistory = newHistory.getHistory();

        assertEquals(4, twoHistory.size(), "Удаление из середины истории не работает!");
        assertEquals(1, twoHistory.getFirst().getId(), "Первая задача в истории не верная!");
        assertEquals(5, twoHistory.getLast().getId(), "Последняя задача в истории не верная!");
        assertFalse(twoHistory.contains(twoTask), "Вторая задача из истории не удалена!");

        newHistory.remove(1);
        List<Task> treeHistory = newHistory.getHistory();
        assertEquals(3, treeHistory.size(), "Удаление head (головы) из истории не работает!");
        assertEquals(threeTask, treeHistory.getFirst(), "При удаление head (головы) из истории 3 элемент головой не стал!");

        newHistory.remove(5);
        List<Task> fourHistory = newHistory.getHistory();
        assertEquals(2, fourHistory.size(), "Удаление tail (хвоста) из истории не работает!");
        assertEquals(threeTask, fourHistory.getFirst(), "При удаление tail (хвоста) из истории 4 элемент хвостом не стал!");
    }

    @Test
    void addNewTask() {
        Task task = new Task(13, "Задача новая", "Описание новой задачи", Status.IN_PROGRESS);
        taskManager.addTask(task);
        final Task savedTask = taskManager.getTaskByID(13);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(4, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(3), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic(13, "Эпик новый", "Описание нового эпика");
        taskManager.addEpic(epic);
        final Epic savedEpic = taskManager.getEpicByID(13);

        assertNotNull(savedEpic, "Эпик не найдена.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(4, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(3), "Эпики не совпадают.");
    }

    @Test
    void addNewSubtask() {
        Subtask subtask = new Subtask(13, "Подзадача новый", "Описание новой подзадачи", 6);
        taskManager.addSubtask(6, subtask);
        final Subtask savedSubtask = taskManager.getSubtaskByID(13);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(7, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(6), "Подзадачи не совпадают.");
    }

    @Test
    void checkEpicStatus() {
        List<Epic> epics = taskManager.getAllEpics();

        assertEquals(Status.NEW, epics.getFirst().getStatus(), "Неверный статус эпика.");
        assertEquals(Status.DONE, epics.getLast().getStatus(), "Неверный статус эпика.");

        Subtask oneUpdateSubtask = new Subtask(9, "Подзадача 3 эпика 1 обновлена", "Описание подзадачи 3 эпика 1 обновлено", Status.IN_PROGRESS, 4);
        Subtask twoUpdateSubtask = new Subtask(12, "Подзадача 1 эпика 3 обновлена", "Описание подзадачи 1 эпика 3 обновлено", Status.IN_PROGRESS, 6);
        taskManager.updateSubtaskByID(oneUpdateSubtask);
        taskManager.updateSubtaskByID(twoUpdateSubtask);

        assertEquals(Status.IN_PROGRESS, epics.getFirst().getStatus(), "Неверный статус эпика.");
        assertEquals(Status.IN_PROGRESS, epics.getLast().getStatus(), "Неверный статус эпика.");
    }

    @Test
    void deleteAllSubtasksAndCheckEpicStatus() {
        taskManager.deleteAllSubtasks();
        assertEquals(0, taskManager.getAllSubtasks().size(), "Не все подзадачи удалены.");

        List<Epic> epics = taskManager.getAllEpics();
        for (Epic epic : epics) {
            assertEquals(Status.NEW, epic.getStatus(), "Неверный статус эпика.");
            assertNull(epic.getSubtaskIdList(), "Не все идентификаторы подзадач удалены из эпика - " + epic.getId());
        }
    }

}