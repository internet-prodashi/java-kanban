import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    TaskManager taskManager = Managers.getDefault();

    @Test
    void testHistoryManager() {
        LocalDateTime expectedStartTime1 = LocalDateTime.of(2025, 4, 1, 23, 7);
        Duration expectedDuration1 = Duration.ofMinutes(150);
        LocalDateTime expectedStartTime2 = LocalDateTime.of(2025, 4, 2, 23, 7);
        Duration expectedDuration2 = Duration.ofMinutes(300);
        LocalDateTime expectedStartTime3 = LocalDateTime.of(2025, 4, 3, 22, 7);
        Duration expectedDuration3 = Duration.ofMinutes(90);
        LocalDateTime expectedStartTime4 = LocalDateTime.of(2025, 4, 4, 23, 7);
        Duration expectedDuration4 = Duration.ofMinutes(150);
        LocalDateTime expectedStartTime5 = LocalDateTime.of(2025, 4, 5, 23, 7);
        Duration expectedDuration5 = Duration.ofMinutes(300);
        LocalDateTime expectedStartTime6 = LocalDateTime.of(2025, 4, 6, 22, 7);
        Duration expectedDuration6 = Duration.ofMinutes(90);
        LocalDateTime expectedStartTime7 = LocalDateTime.of(2025, 4, 7, 23, 7);
        Duration expectedDuration7 = Duration.ofMinutes(150);
        LocalDateTime expectedStartTime8 = LocalDateTime.of(2025, 4, 8, 23, 7);
        Duration expectedDuration8 = Duration.ofMinutes(300);
        LocalDateTime expectedStartTime9 = LocalDateTime.of(2025, 4, 9, 22, 7);
        Duration expectedDuration9 = Duration.ofMinutes(90);
        LocalDateTime expectedStartTime10 = LocalDateTime.of(2025, 4, 10, 23, 7);
        Duration expectedDuration10 = Duration.ofMinutes(150);
        LocalDateTime expectedStartTime11 = LocalDateTime.of(2025, 4, 25, 23, 7);
        Duration expectedDuration11 = Duration.ofMinutes(300);

        Task oneTask = new Task("Задача 1", "Описание задачи 1", expectedStartTime1, expectedDuration1);
        Task twoTask = new Task("Задача 2", "Описание задачи 2", expectedStartTime2, expectedDuration2);
        Task threeTask = new Task("Задача 3", "Описание задачи 3", expectedStartTime3, expectedDuration3);
        taskManager.addTask(oneTask);
        taskManager.addTask(twoTask);
        taskManager.addTask(threeTask);

        Epic oneEpic = new Epic("Эпик 1", "Описание эпика 1");
        Epic twoEpic = new Epic("Эпик 2", "Описание эпика 2");
        Epic threeEpic = new Epic("Эпик 3", "Описание эпика 3");
        taskManager.addEpic(oneEpic);
        taskManager.addEpic(twoEpic);
        taskManager.addEpic(threeEpic);

        Subtask oneSubtaskForOneEpic = new Subtask("Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1", Status.NEW, 4, expectedStartTime4, expectedDuration4);
        Subtask twoSubtaskForOneEpic = new Subtask("Подзадача 2 эпика 1", "Описание подзадачи 2 эпика 1", Status.NEW, 4, expectedStartTime5, expectedDuration5);
        Subtask threeSubtaskForOneEpic = new Subtask("Подзадача 3 эпика 1", "Описание подзадачи 3 эпика 1", Status.NEW, 4, expectedStartTime6, expectedDuration6);
        Subtask oneSubtaskForTwoEpic = new Subtask("Подзадача 1 эпика 2", "Описание подзадачи 1 эпика 2", Status.IN_PROGRESS, 5, expectedStartTime7, expectedDuration7);
        Subtask twoSubtaskForTwoEpic = new Subtask("Подзадача 2 эпика 2", "Описание подзадачи 2 эпика 2", Status.IN_PROGRESS, 5, expectedStartTime8, expectedDuration8);
        Subtask oneSubtaskForThreeEpic = new Subtask("Подзадача 1 эпика 3", "Описание подзадачи 1 эпика 3", Status.DONE, 6, expectedStartTime9, expectedDuration9);
        taskManager.addSubtask(4, oneSubtaskForOneEpic);
        taskManager.addSubtask(4, twoSubtaskForOneEpic);
        taskManager.addSubtask(4, threeSubtaskForOneEpic);
        taskManager.addSubtask(5, oneSubtaskForTwoEpic);
        taskManager.addSubtask(5, twoSubtaskForTwoEpic);
        taskManager.addSubtask(6, oneSubtaskForThreeEpic);

        Task fourTask = new Task("Задача 4", "Описание задачи 4", expectedStartTime10, expectedDuration10);
        Task fiveTask = new Task("Задача 5", "Описание задачи 5", expectedStartTime11, expectedDuration11);
        taskManager.addTask(fourTask);
        taskManager.addTask(fiveTask);

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

        Task firstHistoryTask = taskManager.getTaskByID(1);
        Epic fourHistoryTask = taskManager.getEpicByID(6);
        Subtask lastHistoryTask = taskManager.getSubtaskByID(12);

        assertEquals(firstHistoryTask, history.getFirst(), "Неверная первая задача в истории");
        assertEquals(fourHistoryTask, history.get(history.size() - 7), "Неверная четвертая задача в истории");
        assertEquals(lastHistoryTask, history.getLast(), "Неверная последняя задача в истории");

        assertTrue(history.containsAll(allEpics), "Эпики не найдены в истории");
        assertTrue(history.containsAll(allSubtasks), "Подзадачи не найдены в истории");

        Subtask subtaskUpdate = new Subtask(12, "Подзадача 1 эпика 3 обновлена", "Описание подзадачи 1 эпика 3 обновлен0", Status.IN_PROGRESS, 6);
        taskManager.updateSubtaskByID(subtaskUpdate);

        assertNotEquals(lastHistoryTask.getTitle(), subtaskUpdate.getTitle(), "Неверное название подзадачи");
        assertNotEquals(lastHistoryTask.getDescription(), subtaskUpdate.getDescription(), "Неверное описание подзадачи");
    }
}
