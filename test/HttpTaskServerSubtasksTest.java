import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerSubtasksTest {
    private static TaskManager taskManager;
    private static HttpTaskServer httpTaskServer;
    private static Gson gson;
    private static HttpClient httpClient;

    @BeforeAll
    static void beforeAll() throws IOException {
        taskManager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(taskManager);
        gson = httpTaskServer.getGson();
        httpTaskServer.start();
        httpClient = HttpClient.newHttpClient();
    }

    @AfterAll
    static void afterAll() {
        httpTaskServer.stop();
        httpClient.close();
    }

    @BeforeEach
    void beforeEach() {
        taskManager.deleteAllTask();
        taskManager.deleteAllEpic();
        taskManager.setNewId(1);
    }

    @Test
    void testReturnAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic(1, "Заголовок эпика 1", "Подробное описание эпика 1", Status.NEW);
        taskManager.addEpic(epic1);

        LocalDateTime expectedStartTime1 = LocalDateTime.of(2025, 4, 27, 23, 7);
        Duration expectedDuration1 = Duration.ofMinutes(300);
        Subtask subtask1 = new Subtask(2, "Подзадача 1 эпика 1", "описание подзадачи 1 эпика 1", 1, expectedStartTime1, expectedDuration1);
        System.out.println(taskManager.getAllSubtasks());
        taskManager.addSubtask(1, subtask1);

        String subtask1Json = gson.toJson(subtask1);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        assertEquals(200, response.statusCode(), "Неверный код ответа сервера");
        assertTrue(responseBody.contains(subtask1Json), "Нет добавленной подзадачи в теле ответа сервера");

        taskManager.deleteAllSubtasks();

        HttpResponse<String> responseNew = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBodyNew = responseNew.body();

        assertEquals(200, responseNew.statusCode(), "Неверный код ответа сервера после удаления всех подзадач");
        assertEquals("[]", responseBodyNew, "Тело ответа сервера не пустое после удаления всех подзадач");
    }

    @Test
    void testReturnEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic(1, "Заголовок эпика 1", "Подробное описание эпика 1", Status.NEW);
        taskManager.addEpic(epic1);

        LocalDateTime expectedStartTime1 = LocalDateTime.of(2025, 4, 27, 23, 7);
        Duration expectedDuration1 = Duration.ofMinutes(300);
        Subtask subtask1 = new Subtask(2, "Подзадача 1 эпика 1", "описание подзадачи 1 эпика 1", 1, expectedStartTime1, expectedDuration1);
        taskManager.addSubtask(1, subtask1);

        String subtask1Json = gson.toJson(subtask1);

        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        assertEquals(200, response.statusCode(), "Неверный код ответа сервера");
        assertTrue(responseBody.contains(subtask1Json), "Нет добавленной подзадачи в теле ответа сервера");

        taskManager.deleteAllSubtasks();

        HttpResponse<String> responseNew = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBodyNew = responseNew.body();

        assertEquals(404, responseNew.statusCode(), "Неверный код ответа сервера после удаления всех подзадач");
        assertEquals("Подзадача с идентификатором 2 не найдена", responseBodyNew, "Тело ответа сервера не пустое после удаления всех подзадач");
    }

    @Test
    void testAddSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic(1, "Заголовок эпика 1", "Подробное описание эпика 1", Status.NEW);
        taskManager.addEpic(epic1);

        LocalDateTime expectedStartTime1 = LocalDateTime.of(2025, 4, 27, 23, 7);
        Duration expectedDuration1 = Duration.ofMinutes(300);
        Subtask subtask1 = new Subtask(2, "Подзадача 1 эпика 1", "описание подзадачи 1 эпика 1", 1, expectedStartTime1, expectedDuration1);

        String subtask1Json = gson.toJson(subtask1);

        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtask1Json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный код ответа сервера");

        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();

        assertNotNull(tasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Подзадача 1 эпика 1", tasksFromManager.getFirst().getTitle(), "Некорректное имя подзадачи");
    }

    @Test
    void testUpdateSubtaskById() throws IOException, InterruptedException {
        Epic epic1 = new Epic(1, "Заголовок эпика 1", "Подробное описание эпика 1", Status.NEW);
        taskManager.addEpic(epic1);

        LocalDateTime expectedStartTime1 = LocalDateTime.of(2025, 4, 27, 23, 7);
        Duration expectedDuration1 = Duration.ofMinutes(300);
        Subtask subtask1 = new Subtask(2, "Подзадача 1 эпика 1", "описание подзадачи 1 эпика 1", 1, expectedStartTime1, expectedDuration1);
        taskManager.addSubtask(1, subtask1);

        Subtask subtask1Update = new Subtask(2, "Подзадача 1 эпика 1 изменена", "описание подзадачи 1 эпика 1 изменено", 1, expectedStartTime1, expectedDuration1);

        String subtask1UpdateJson = gson.toJson(subtask1Update);

        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtask1UpdateJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный код ответа сервера");

        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();

        assertNotNull(tasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Подзадача 1 эпика 1 изменена", tasksFromManager.getFirst().getTitle(), "Имя подзадачи не обновилось");
        assertEquals("описание подзадачи 1 эпика 1 изменено", tasksFromManager.getFirst().getDescription(), "Описание подзадачи не обновилось");

        Subtask subtask2Update = new Subtask(3, "Подзадача 1 эпика 1 изменена", "описание подзадачи 1 эпика 1 изменено", 1, expectedStartTime1, expectedDuration1);

        String subtask2UpdateJson = gson.toJson(subtask2Update);

        URI urlNew = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest requestNew = HttpRequest
                .newBuilder()
                .uri(urlNew)
                .POST(HttpRequest.BodyPublishers.ofString(subtask2UpdateJson))
                .build();

        HttpResponse<String> responseNew = httpClient.send(requestNew, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseNew.statusCode(), "Неверный код ответа сервера");

        LocalDateTime expectedStartTime2 = LocalDateTime.of(2025, 4, 28, 23, 7);
        Duration expectedDuration2 = Duration.ofMinutes(180);
        Subtask subtask3 = new Subtask(3, "Подзадача 2 эпика 1", "описание подзадачи 2 эпика 1", 1, expectedStartTime2, expectedDuration2);
        taskManager.addSubtask(1, subtask3);

        LocalDateTime expectedStartTime2Update = LocalDateTime.of(2025, 4, 27, 23, 7);
        Duration expectedDuration2Update = Duration.ofMinutes(300);
        Subtask subtask3Update = new Subtask(3, "Подзадача 2 эпика 1", "описание подзадачи 2 эпика 1", 1, expectedStartTime2Update, expectedDuration2Update);

        String subtask3UpdateJson = gson.toJson(subtask3Update);

        HttpRequest requestNewTwo = HttpRequest
                .newBuilder()
                .uri(urlNew)
                .POST(HttpRequest.BodyPublishers.ofString(subtask3UpdateJson))
                .build();

        HttpResponse<String> responseNewTwo = httpClient.send(requestNewTwo, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, responseNewTwo.statusCode(), "Неверный код ответа сервера");
    }

    @Test
    void testDeleteAllSubtusks() throws IOException, InterruptedException {
        Epic epic1 = new Epic(1, "Заголовок эпика 1", "Подробное описание эпика 1", Status.NEW);
        taskManager.addEpic(epic1);

        LocalDateTime expectedStartTime1 = LocalDateTime.of(2025, 4, 27, 23, 7);
        Duration expectedDuration1 = Duration.ofMinutes(300);
        Subtask subtask1 = new Subtask(2, "Подзадача 1 эпика 1", "описание подзадачи 1 эпика 1", 1, expectedStartTime1, expectedDuration1);
        taskManager.addSubtask(1, subtask1);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный код ответа сервера");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Не все подзадачи удалены");
    }

    @Test
    void testDeleteEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic(1, "Заголовок эпика 1", "Подробное описание эпика 1", Status.NEW);
        taskManager.addEpic(epic1);

        LocalDateTime expectedStartTime1 = LocalDateTime.of(2025, 4, 27, 23, 7);
        Duration expectedDuration1 = Duration.ofMinutes(300);
        Subtask subtask1 = new Subtask(2, "Подзадача 1 эпика 1", "описание подзадачи 1 эпика 1", 1, expectedStartTime1, expectedDuration1);
        taskManager.addSubtask(1, subtask1);

        URI url = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Неверный код ответа сервера");

        URI urlNew = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest requestNew = HttpRequest
                .newBuilder()
                .uri(urlNew)
                .DELETE()
                .build();

        HttpResponse<String> responseNew = httpClient.send(requestNew, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseNew.statusCode(), "Неверный код ответа сервера");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Эпик 1 не удален");
    }

}