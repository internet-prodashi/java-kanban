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

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerHistoryTest {
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
    void testReturnAllTypesTasksInHistory() throws IOException, InterruptedException {
        LocalDateTime expectedStartTime1 = LocalDateTime.of(2025, 4, 25, 23, 7);
        Duration expectedDuration1 = Duration.ofMinutes(150);
        Task task1 = new Task(1, "Задача 1", "описание задачи 1", expectedStartTime1, expectedDuration1);
        taskManager.addTask(task1);

        Epic epic1 = new Epic(2, "Заголовок эпика 1", "Подробное описание эпика 1", Status.NEW);
        taskManager.addEpic(epic1);

        LocalDateTime expectedStartTime2 = LocalDateTime.of(2025, 4, 27, 23, 7);
        Duration expectedDuration2 = Duration.ofMinutes(300);
        Subtask subtask1 = new Subtask(3, "Подзадача 1 эпика 1", "описание подзадачи 1 эпика 1", 2, expectedStartTime2, expectedDuration2);
        taskManager.addSubtask(2, subtask1);

        URI urlTask = URI.create("http://localhost:8080/tasks/1");
        HttpRequest requestTask = HttpRequest
                .newBuilder()
                .uri(urlTask)
                .GET()
                .build();
        httpClient.send(requestTask, HttpResponse.BodyHandlers.ofString());

        URI urlEpic = URI.create("http://localhost:8080/epics/2");
        HttpRequest requestEpic = HttpRequest
                .newBuilder()
                .uri(urlEpic)
                .GET()
                .build();
        httpClient.send(requestEpic, HttpResponse.BodyHandlers.ofString());

        URI urlSubtask = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest requestSubtask = HttpRequest
                .newBuilder()
                .uri(urlSubtask)
                .GET()
                .build();
        httpClient.send(requestSubtask, HttpResponse.BodyHandlers.ofString());

        URI urlHistory = URI.create("http://localhost:8080/history");
        HttpRequest requestHistory = HttpRequest
                .newBuilder()
                .uri(urlHistory)
                .GET()
                .build();
        HttpResponse<String> responseHistory = httpClient.send(requestHistory, HttpResponse.BodyHandlers.ofString());
        String bodyHistory = responseHistory.body();

        assertEquals(200, responseHistory.statusCode(), "Неверный код ответа сервера");
        assertTrue(bodyHistory.contains(gson.toJson(task1)), "Нет просмотренной задачи в теле ответа сервера");
        assertTrue(bodyHistory.contains(gson.toJson(taskManager.getEpicByID(2))), "Нет просмотренного эпика в теле ответа сервера");
        assertTrue(bodyHistory.contains(gson.toJson(subtask1)), "Нет просмотренной подзадачи в теле ответа сервера");
    }

}