import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
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

    // Тесты дата и время
    @Test
    void allTypesTasksHaveCorrectTimeParameters() {
        TaskManager taskManagerTime = Managers.getDefault();

        LocalDateTime expectedStartTime1 = LocalDateTime.of(2025, 4, 28, 23, 7);
        Duration expectedDuration1 = Duration.ofMinutes(150);
        LocalDateTime expectedEndTime1 = expectedStartTime1.plus(expectedDuration1);
        LocalDateTime expectedStartTime2 = LocalDateTime.of(2025, 4, 25, 23, 7);
        Duration expectedDuration2 = Duration.ofMinutes(300);
        LocalDateTime expectedEndTime2 = expectedStartTime2.plus(expectedDuration2);
        LocalDateTime expectedStartTime3 = LocalDateTime.of(2025, 4, 27, 22, 7);
        Duration expectedDuration3 = Duration.ofMinutes(90);
        LocalDateTime expectedEndTime3 = expectedStartTime3.plus(expectedDuration3);

        Task task1 = new Task("Задачи 1", "описание задачи 1", expectedStartTime1, expectedDuration1);
        taskManagerTime.addTask(task1);
        Epic epic1 = new Epic("Заголовок эпика 1", "Подробное описание эпика 1");
        taskManagerTime.addEpic(epic1);

        assertNull(taskManagerTime.getEpicByID(2).getStartTime(), "До добавления подзадачи у эпика время начала не пустое");
        assertNull(taskManagerTime.getEpicByID(2).getDuration(), "До добавления подзадачи у эпика продолжительность не пустая");
        assertNull(taskManagerTime.getEpicByID(2).getEndTime(), "До добавления подзадачи у эпика время окончания не пустое");

        Subtask subtask1 = new Subtask("Подзадача 1 эпика 1", "описание подзадачи 1 эпика 1", 2, expectedStartTime2, expectedDuration2);
        taskManagerTime.addSubtask(2, subtask1);
        Subtask subtask2 = new Subtask("Подзадача 2 эпика 1", "описание подзадачи 2 эпика 1", 2, expectedStartTime3, expectedDuration3);
        taskManagerTime.addSubtask(2, subtask2);

        assertEquals(expectedStartTime1, task1.getStartTime(), "У задачи неверное время начала");
        assertEquals(expectedDuration1, task1.getDuration(), "У задачи неверная продолжительность");
        assertEquals(expectedEndTime1, task1.getEndTime(), "У задачи неверное время окончания");

        assertEquals(expectedStartTime2, subtask1.getStartTime(), "У подзадачи неверное время начала");
        assertEquals(expectedDuration2, subtask1.getDuration(), "У подзадачи неверная продолжительность");
        assertEquals(expectedEndTime2, subtask1.getEndTime(), "У подзадачи неверное время окончания");

        assertEquals(expectedStartTime2, taskManagerTime.getEpicByID(2).getStartTime(), "У эпика неверное время начала");
        assertEquals(expectedDuration2.plus(expectedDuration3), taskManagerTime.getEpicByID(2).getDuration(), "У эпика неверная продолжительность");
        assertEquals(expectedEndTime3, taskManagerTime.getEpicByID(2).getEndTime(), "У эпика неверное время окончания");

        taskManagerTime.deleteSubtaskByID(4);

        assertEquals(expectedStartTime2, taskManagerTime.getEpicByID(2).getStartTime(), "После удаления подзадачи у эпика неверное время начала");
        assertEquals(expectedDuration2, taskManagerTime.getEpicByID(2).getDuration(), "После удаления подзадачи у эпика неверная продолжительность");
        assertEquals(expectedEndTime2, taskManagerTime.getEpicByID(2).getEndTime(), "После удаления подзадачи у эпика неверное время окончания");

        LocalDateTime expectedStartTime4 = LocalDateTime.of(2024, 4, 27, 22, 7);
        Duration expectedDuration4 = Duration.ofMinutes(900);
        LocalDateTime expectedEndTime4 = expectedStartTime4.plus(expectedDuration4);
        Subtask subtask4 = new Subtask(3, "Подзадача 1 эпика 1", "описание подзадачи 1 эпика 1", 2, expectedStartTime4, expectedDuration4);
        taskManagerTime.updateSubtaskByID(subtask4);

        assertEquals(expectedStartTime4, taskManagerTime.getEpicByID(2).getStartTime(), "После обновления подзадачи у эпика неверное время начала");
        assertEquals(expectedDuration4, taskManagerTime.getEpicByID(2).getDuration(), "После обновления подзадачи у эпика неверная продолжительность");
        assertEquals(expectedEndTime4, taskManagerTime.getEpicByID(2).getEndTime(), "После обновления подзадачи у эпика неверное время окончания");

        Subtask subtask5 = new Subtask("Подзадача 3 эпика 1", "описание подзадачи 3 эпика 1", 2);
        taskManagerTime.updateSubtaskByID(subtask5);

        assertEquals(expectedStartTime4, taskManagerTime.getEpicByID(2).getStartTime(), "После добавления подзадачи без времени у эпика неверное время начала");
        assertEquals(expectedDuration4, taskManagerTime.getEpicByID(2).getDuration(), "После добавления подзадачи без времени у эпика неверная продолжительность");
        assertEquals(expectedEndTime4, taskManagerTime.getEpicByID(2).getEndTime(), "После добавления подзадачи без времени у эпика неверное время окончания");

        taskManagerTime.deleteAllSubtasks();

        assertNotNull(taskManagerTime.getEpicByID(2).getStartTime(), "После удаления подзадач у эпика время начала не пустое");
        assertNotNull(taskManagerTime.getEpicByID(2).getDuration(), "После удаления подзадач у эпика продолжительность не пустая");
        assertNotNull(taskManagerTime.getEpicByID(2).getEndTime(), "После удаления подзадач у эпика время окончания не пустое");
    }

    @Test
    void getPriorityTasksAndCheckSortingAndAddingIntersection() {
        TaskManager taskManagerTime = Managers.getDefault();

        LocalDateTime expectedStartTime1 = LocalDateTime.of(2025, 4, 28, 23, 7);
        Duration expectedDuration1 = Duration.ofMinutes(60);
        LocalDateTime expectedStartTime2 = LocalDateTime.of(2025, 4, 26, 23, 7);
        Duration expectedDuration2 = Duration.ofMinutes(120);
        LocalDateTime expectedStartTime3 = LocalDateTime.of(2025, 4, 26, 22, 7);
        Duration expectedDuration3 = Duration.ofMinutes(180);
        LocalDateTime expectedStartTime4 = LocalDateTime.of(2025, 4, 27, 22, 7);
        Duration expectedDuration4 = Duration.ofMinutes(240);

        Task task1 = new Task("Задачи 1", "описание задачи 1", expectedStartTime1, expectedDuration1);
        taskManagerTime.addTask(task1);
        Epic epic1 = new Epic("Заголовок эпика 1", "Подробное описание эпика 1");
        taskManagerTime.addEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1 эпика 1", "описание подзадачи 1 эпика 1", 2, expectedStartTime2, expectedDuration2);
        taskManagerTime.addSubtask(2, subtask1);
        Subtask subtask2 = new Subtask("Подзадача 2 эпика 1", "описание подзадачи 2 эпика 1", 2, expectedStartTime3, expectedDuration3);
        taskManagerTime.addSubtask(2, subtask2);
        Subtask subtask3 = new Subtask("Подзадача 3 эпика 1", "описание подзадачи 3 эпика 1", 2, expectedStartTime4, expectedDuration4);
        taskManagerTime.addSubtask(2, subtask3);

        List<Task> prioritizedTasks = taskManagerTime.getPrioritizedTasks();

        assertEquals(taskManagerTime.getSubtaskByID(3), prioritizedTasks.get(0), "Не верная сортировка первой задачи в списке задач");
        assertEquals(taskManagerTime.getSubtaskByID(5), prioritizedTasks.get(1), "Не верная сортировка второй задачи в списке задач");
        assertEquals(taskManagerTime.getTaskByID(1), prioritizedTasks.get(2), "Не верная сортировка третьей задачи в списке задач");

        assertEquals(3, prioritizedTasks.size(), "Не верное количество задач в списке задач");
        assertNotEquals(subtask2.getTitle(), prioritizedTasks.get(2).getTitle(), "Задача с пересекающимся временем попала в список задач");
    }

}