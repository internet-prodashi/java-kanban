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

class HttpTaskServerTasksTest {
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
    void testReturnAllTasks() throws IOException, InterruptedException {
        LocalDateTime expectedStartTime1 = LocalDateTime.of(2025, 4, 25, 23, 7);
        Duration expectedDuration1 = Duration.ofMinutes(150);
        Task task1 = new Task(1, "Задачи 1", "описание задачи 1", expectedStartTime1, expectedDuration1);
        taskManager.addTask(task1);

        String task1Json = gson.toJson(task1);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        assertEquals(200, response.statusCode(), "Неверный код ответа сервера");
        assertTrue(responseBody.contains(task1Json), "Нет добавленной задачи в теле ответа сервера");

        taskManager.deleteAllTask();

        HttpResponse<String> responseNew = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBodyNew = responseNew.body();

        assertEquals(200, responseNew.statusCode(), "Неверный код ответа сервера после удаления всех задач");
        assertEquals("[]", responseBodyNew, "Тело ответа сервера не пустое после удаления всех задач");
    }

    @Test
    void testReturnTaskById() throws IOException, InterruptedException {
        LocalDateTime expectedStartTime1 = LocalDateTime.of(2025, 4, 25, 23, 7);
        Duration expectedDuration1 = Duration.ofMinutes(150);
        Task task1 = new Task(1, "Задачи 1", "описание задачи 1", expectedStartTime1, expectedDuration1);
        taskManager.addTask(task1);

        String task1Json = gson.toJson(task1);

        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        assertEquals(200, response.statusCode(), "Неверный код ответа сервера");
        assertTrue(responseBody.contains(task1Json), "Нет добавленной задачи в теле ответа сервера");

        taskManager.deleteAllTask();

        HttpResponse<String> responseNew = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBodyNew = responseNew.body();

        assertEquals(404, responseNew.statusCode(), "Неверный код ответа сервера после удаления всех задач");
        assertEquals("Задача с идентификатором 1 не найдена", responseBodyNew, "Тело ответа сервера не пустое после удаления всех задач");
    }

    @Test
    void testAddTask() throws IOException, InterruptedException {
        LocalDateTime expectedStartTime1 = LocalDateTime.of(2025, 4, 25, 23, 7);
        Duration expectedDuration1 = Duration.ofMinutes(150);
        Task task1 = new Task(1, "Задача 1", "описание задачи 1", expectedStartTime1, expectedDuration1);

        String task1Json = gson.toJson(task1);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный код ответа сервера");

        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача 1", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    void testUpdateTaskById() throws IOException, InterruptedException {
        LocalDateTime expectedStartTime1 = LocalDateTime.of(2025, 4, 25, 23, 7);
        Duration expectedDuration1 = Duration.ofMinutes(150);
        Task task1 = new Task(1, "Задача 1", "описание задачи 1", expectedStartTime1, expectedDuration1);
        taskManager.addTask(task1);

        Task task1Update = new Task(1, "Задача 1 изменена", "описание задачи 1 изменено", expectedStartTime1, expectedDuration1);

        String task1UpdateJson = gson.toJson(task1Update);

        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(task1UpdateJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный код ответа сервера");

        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача 1 изменена", tasksFromManager.getFirst().getTitle(), "Имя задачи не обновилось");
        assertEquals("описание задачи 1 изменено", tasksFromManager.getFirst().getDescription(), "Описание задачи не обновилось");

        Task task2Update = new Task(2, "Задача 1 изменена", "описание задачи 1 изменено", expectedStartTime1, expectedDuration1);

        String task2UpdateJson = gson.toJson(task2Update);

        URI urlNew = URI.create("http://localhost:8080/tasks/2");
        HttpRequest requestNew = HttpRequest
                .newBuilder()
                .uri(urlNew)
                .POST(HttpRequest.BodyPublishers.ofString(task2UpdateJson))
                .build();

        HttpResponse<String> responseNew = httpClient.send(requestNew, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseNew.statusCode(), "Неверный код ответа сервера");

        LocalDateTime expectedStartTime2 = LocalDateTime.of(2025, 4, 26, 23, 7);
        Duration expectedDuration2 = Duration.ofMinutes(150);
        Task task2 = new Task(2, "Задача 2", "описание задачи 2", expectedStartTime2, expectedDuration2);
        taskManager.addTask(task2);

        LocalDateTime expectedStartTime2Update = LocalDateTime.of(2025, 4, 25, 23, 7);
        Duration expectedDuration2Update = Duration.ofMinutes(180);
        Task task3Update = new Task(2, "Задача 2 изменена", "описание задачи 2 изменено", expectedStartTime2Update, expectedDuration2Update);

        String task3UpdateJson = gson.toJson(task3Update);

        HttpRequest requestNewTwo = HttpRequest
                .newBuilder()
                .uri(urlNew)
                .POST(HttpRequest.BodyPublishers.ofString(task3UpdateJson))
                .build();

        HttpResponse<String> responseNewTwo = httpClient.send(requestNewTwo, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, responseNewTwo.statusCode(), "Неверный код ответа сервера");
    }

    @Test
    void testDeleteAllTasks() throws IOException, InterruptedException {
        LocalDateTime expectedStartTime1 = LocalDateTime.of(2025, 4, 25, 23, 7);
        Duration expectedDuration1 = Duration.ofMinutes(150);
        Task task1 = new Task(1, "Задача 1", "описание задачи 1", expectedStartTime1, expectedDuration1);
        taskManager.addTask(task1);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный код ответа сервера");
        assertTrue(taskManager.getAllTasks().isEmpty(), "Не все задачи удалены");
    }

    @Test
    void testDeleteTaskById() throws IOException, InterruptedException {
        LocalDateTime expectedStartTime1 = LocalDateTime.of(2025, 4, 25, 23, 7);
        Duration expectedDuration1 = Duration.ofMinutes(150);
        Task task1 = new Task(1, "Задача 1", "описание задачи 1", expectedStartTime1, expectedDuration1);
        taskManager.addTask(task1);

        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Неверный код ответа сервера");

        URI urlNew = URI.create("http://localhost:8080/tasks/1");
        HttpRequest requestNew = HttpRequest
                .newBuilder()
                .uri(urlNew)
                .DELETE()
                .build();

        HttpResponse<String> responseNew = httpClient.send(requestNew, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseNew.statusCode(), "Неверный код ответа сервера");
        assertTrue(taskManager.getAllTasks().isEmpty(), "Задача 1 не удалена");
    }

}