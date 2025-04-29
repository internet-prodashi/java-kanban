import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Test
    void numberOfTasksShouldBeEqualWhenSavingAndLoadingFromFile() throws IOException {

        LocalDateTime expectedStartTime1 = LocalDateTime.of(2025, 4, 28, 23, 7);
        Duration expectedDuration1 = Duration.ofMinutes(30);
        LocalDateTime expectedStartTime2 = LocalDateTime.of(2025, 4, 27, 23, 7);
        Duration expectedDuration2 = Duration.ofMinutes(60);
        LocalDateTime expectedStartTime3 = LocalDateTime.of(2025, 4, 29, 22, 7);
        Duration expectedDuration3 = Duration.ofMinutes(90);
        LocalDateTime expectedStartTime4 = LocalDateTime.of(2025, 3, 25, 23, 7);
        Duration expectedDuration4 = Duration.ofMinutes(120);
        LocalDateTime expectedStartTime5 = LocalDateTime.of(2025, 3, 28, 23, 7);
        Duration expectedDuration5 = Duration.ofMinutes(150);
        LocalDateTime expectedStartTime6 = LocalDateTime.of(2025, 3, 27, 23, 7);
        Duration expectedDuration6 = Duration.ofMinutes(180);
        LocalDateTime expectedStartTime7 = LocalDateTime.of(2025, 2, 27, 22, 7);
        Duration expectedDuration7 = Duration.ofMinutes(210);
        LocalDateTime expectedStartTime8 = LocalDateTime.of(2025, 1, 25, 23, 7);
        Duration expectedDuration8 = Duration.ofMinutes(240);
        LocalDateTime expectedStartTime9 = LocalDateTime.of(2025, 6, 25, 23, 7);
        Duration expectedDuration9 = Duration.ofMinutes(270);

        File tempFile = File.createTempFile("Temp_file", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);

        Task oneTask = new Task("Задача 1", "Описание задачи 1", expectedStartTime1, expectedDuration1);
        Task twoTask = new Task("Задача 2", "Описание задачи 2", expectedStartTime2, expectedDuration2);
        Task threeTask = new Task("Задача 3", "Описание задачи 3", expectedStartTime3, expectedDuration3);
        fileBackedTaskManager.addTask(oneTask);
        fileBackedTaskManager.addTask(twoTask);
        fileBackedTaskManager.addTask(threeTask);

        Epic oneEpic = new Epic("Эпик 1", "Описание эпика 1");
        Epic twoEpic = new Epic("Эпик 2", "Описание эпика 2");
        Epic threeEpic = new Epic("Эпик 3", "Описание эпика 3");
        fileBackedTaskManager.addEpic(oneEpic);
        fileBackedTaskManager.addEpic(twoEpic);
        fileBackedTaskManager.addEpic(threeEpic);

        Subtask oneSubtaskForOneEpic = new Subtask("Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1", Status.NEW, 4, expectedStartTime4, expectedDuration4);
        Subtask twoSubtaskForOneEpic = new Subtask("Подзадача 2 эпика 1", "Описание подзадачи 2 эпика 1", Status.NEW, 4, expectedStartTime5, expectedDuration5);
        Subtask threeSubtaskForOneEpic = new Subtask("Подзадача 3 эпика 1", "Описание подзадачи 3 эпика 1", Status.NEW, 4, expectedStartTime6, expectedDuration6);
        Subtask oneSubtaskForTwoEpic = new Subtask("Подзадача 1 эпика 2", "Описание подзадачи 1 эпика 2", Status.IN_PROGRESS, 5, expectedStartTime7, expectedDuration7);
        Subtask twoSubtaskForTwoEpic = new Subtask("Подзадача 2 эпика 2", "Описание подзадачи 2 эпика 2", Status.IN_PROGRESS, 5, expectedStartTime8, expectedDuration8);
        Subtask oneSubtaskForThreeEpic = new Subtask("Подзадача 1 эпика 3", "Описание подзадачи 1 эпика 3", Status.DONE, 6, expectedStartTime9, expectedDuration9);
        fileBackedTaskManager.addSubtask(4, oneSubtaskForOneEpic);
        fileBackedTaskManager.addSubtask(4, twoSubtaskForOneEpic);
        fileBackedTaskManager.addSubtask(4, threeSubtaskForOneEpic);
        fileBackedTaskManager.addSubtask(5, oneSubtaskForTwoEpic);
        fileBackedTaskManager.addSubtask(5, twoSubtaskForTwoEpic);
        fileBackedTaskManager.addSubtask(6, oneSubtaskForThreeEpic);

        FileBackedTaskManager loadFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> loadTasks = loadFileBackedTaskManager.getAllTasks();
        assertEquals(3, loadTasks.size(), "Загружено должно было быть три задача");
        Task loadFirstTask = loadTasks.getFirst();
        assertEquals(fileBackedTaskManager.getTaskByID(1).getId(), loadFirstTask.getId(), "Идентификатор первой задачи не совпадает с ее идентификатором из файла");
        assertEquals(oneTask.getTitle(), loadFirstTask.getTitle(), "Название первой задачи не совпадает с ее названием из файла");
        assertEquals(oneTask.getDescription(), loadFirstTask.getDescription(), "Описание первой задачи не совпадает с ее описанием из файла");
        assertEquals(oneTask.getStatus(), loadFirstTask.getStatus(), "Статус первой задачи не совпадает с ее стутусом из файла");

        List<Epic> loadEpics = loadFileBackedTaskManager.getAllEpics();
        assertEquals(3, loadEpics.size(), "Загружено должно было быть три эпика");
        Epic loadFirstEpic = loadEpics.getFirst();
        assertEquals(fileBackedTaskManager.getEpicByID(4).getId(), loadFirstEpic.getId(), "Идентификатор первого эпика не совпадает с его идентификатором из файла");
        assertEquals(oneEpic.getTitle(), loadFirstEpic.getTitle(), "Название первого эпика не совпадает с его названием из файла");
        assertEquals(oneEpic.getDescription(), loadFirstEpic.getDescription(), "Описание первого эпика не совпадает с его описанием из файла");
        assertEquals(oneEpic.getStatus(), loadFirstEpic.getStatus(), "Статус первого эпика не совпадает с его стутусом из файла");
        assertEquals(fileBackedTaskManager.getEpicByID(4).getSubtaskIdList(), loadFirstEpic.getSubtaskIdList(), "Список идентификаторов подзадач первого эпика не совпадает с его списком подзадач из файла");

        List<Subtask> loadSubtasks = loadFileBackedTaskManager.getAllSubtasks();
        assertEquals(6, loadSubtasks.size(), "Загружено должно было быть шесть подзадач");
        Subtask loadFirstSubtask = loadSubtasks.getFirst();
        assertEquals(fileBackedTaskManager.getSubtaskByID(7).getId(), loadFirstSubtask.getId(), "Идентификатор первой подзадачи не совпадает с ее идентификатором из файла");
        assertEquals(oneSubtaskForOneEpic.getTitle(), loadFirstSubtask.getTitle(), "Название первой подзадачи не совпадает с ее названием из файла");
        assertEquals(oneSubtaskForOneEpic.getDescription(), loadFirstSubtask.getDescription(), "Описание первой подзадачи не совпадает с ее описанием из файла");
        assertEquals(oneSubtaskForOneEpic.getStatus(), loadFirstSubtask.getStatus(), "Статус первой подзадачи не совпадает с ее стутусом из файла");
        assertEquals(oneSubtaskForOneEpic.getEpicId(), loadFirstSubtask.getEpicId(), "Идентификатор эпика первой подзадачи не совпадает с ее идентификатором эпика из файла");

    }

    @Test
    void loadFromEmptyFile() throws IOException {
        File tempFile = File.createTempFile("Temp_empty_file", ".csv");
        tempFile.deleteOnExit();

        Files.writeString(tempFile.toPath(), "id,name,type,status,description,starttime,duration,endtime,epic");
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(tempFile), "Не верный заголовок в файле csv");

        Files.writeString(tempFile.toPath(), "id,type,name,status,description,starttime,duration,endtime,epic");
        FileBackedTaskManager loadFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertNotNull(loadFileBackedTaskManager, "Менеджер задач не должен быть равен null");
        assertEquals(0, loadFileBackedTaskManager.getAllTasks().size(), "Список задач не пустой");
        assertEquals(0, loadFileBackedTaskManager.getAllEpics().size(), "Список эпиков не пустой");
        assertEquals(0, loadFileBackedTaskManager.getAllSubtasks().size(), "Список подзадач не пустой");
    }

    @Test
    void saveEmptyManagerInFile() throws IOException {
        LocalDateTime expectedStartTime1 = LocalDateTime.of(2025, 4, 28, 23, 7);
        Duration expectedDuration1 = Duration.ofMinutes(30);

        File tempFile = File.createTempFile("Temp_file", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager emptyFileBackedTaskManager = new FileBackedTaskManager(tempFile);
        Task oneTask = new Task("Задача 1", "Описание задачи 1", expectedStartTime1, expectedDuration1);
        emptyFileBackedTaskManager.addTask(oneTask); // Добавили задачу (файл сформировался)
        emptyFileBackedTaskManager.deleteAllTask(); // И удалили задачу (в файле только заголовок)

        assertTrue(tempFile.exists(), "Файл не существует");
        assertTrue(tempFile.length() > 0, "Файл не пустой, есть заголовок: id,type,name,status,description,epic");

        FileBackedTaskManager loadFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertNotNull(loadFileBackedTaskManager, "При загрузке из файле менеджер не равен null");
        assertEquals(0, loadFileBackedTaskManager.getAllTasks().size(), "Список задач не пустой");
        assertEquals(0, loadFileBackedTaskManager.getAllEpics().size(), "Список эпиков не пустой");
        assertEquals(0, loadFileBackedTaskManager.getAllSubtasks().size(), "Список подзадач не пустой");
    }

}
