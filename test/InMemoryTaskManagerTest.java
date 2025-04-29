import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Test
    void getAndCalculateAllTasksAllType() {
        TaskManager taskManagerTime = Managers.getDefault();

        LocalDateTime expectedStartTime1 = LocalDateTime.of(2025, 4, 28, 23, 7);
        Duration expectedDuration1 = Duration.ofMinutes(150);
        LocalDateTime expectedStartTime2 = LocalDateTime.of(2025, 4, 25, 23, 7);
        Duration expectedDuration2 = Duration.ofMinutes(300);
        LocalDateTime expectedStartTime3 = LocalDateTime.of(2025, 4, 27, 22, 7);
        Duration expectedDuration3 = Duration.ofMinutes(90);

        Task task1 = new Task("Задачи 1", "описание задачи 1", expectedStartTime1, expectedDuration1);
        taskManagerTime.addTask(task1);
        Epic epic1 = new Epic("Заголовок эпика 1", "Подробное описание эпика 1");
        taskManagerTime.addEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1 эпика 1", "описание подзадачи 1 эпика 1", 2, expectedStartTime2, expectedDuration2);
        taskManagerTime.addSubtask(2, subtask1);
        Subtask subtask2 = new Subtask("Подзадача 2 эпика 1", "описание подзадачи 2 эпика 1", 2, expectedStartTime3, expectedDuration3);
        taskManagerTime.addSubtask(2, subtask2);

        List<List<Task>> allTasksAllType = taskManagerTime.getAllTasksAllType();

        assertEquals(3, allTasksAllType.size(), "Не верное количество типов задач");
        assertEquals(1, allTasksAllType.get(0).size(), "Не верное количество задач");
        assertEquals(1, allTasksAllType.get(1).size(), "Не верное количество эпиков");
        assertEquals(2, allTasksAllType.get(2).size(), "Не верное количество подзадач");
    }

}