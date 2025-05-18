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

class HttpTaskServerPrioritizedTest {
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
    void testReturnCorrectSequenceTasksInPriorityList() throws IOException, InterruptedException {
        LocalDateTime expectedStartTime1 = LocalDateTime.of(2025, 4, 25, 23, 7);
        Duration expectedDuration1 = Duration.ofMinutes(150);
        Task task1 = new Task(1, "Задача 1", "описание задачи 1", expectedStartTime1, expectedDuration1);
        taskManager.addTask(task1);

        LocalDateTime expectedStartTime2 = LocalDateTime.of(2025, 4, 24, 23, 7);
        Duration expectedDuration2 = Duration.ofMinutes(250);
        Task task2 = new Task(2, "Задача 2", "описание задачи 2", expectedStartTime2, expectedDuration2);
        taskManager.addTask(task2);

        LocalDateTime expectedStartTime3 = LocalDateTime.of(2025, 4, 26, 23, 7);
        Duration expectedDuration3 = Duration.ofMinutes(350);
        Task task3 = new Task(3, "Задача 3", "описание задачи 3", expectedStartTime3, expectedDuration3);
        taskManager.addTask(task3);

        URI urlPrioritized = URI.create("http://localhost:8080/prioritized");
        HttpRequest requestPrioritized = HttpRequest
                .newBuilder()
                .uri(urlPrioritized)
                .GET()
                .build();
        HttpResponse<String> responsePrioritized = httpClient.send(requestPrioritized, HttpResponse.BodyHandlers.ofString());
        String bodyPrioritized = responsePrioritized.body();

        assertEquals(200, responsePrioritized.statusCode(), "Неверный код ответа сервера");
        assertTrue(bodyPrioritized.contains(gson.toJson(task1)), "Нет задачи 1 в теле ответа сервера");
        assertTrue(bodyPrioritized.contains(gson.toJson(task2)), "Нет задачи 2 в теле ответа сервера");
        assertTrue(bodyPrioritized.contains(gson.toJson(task3)), "Нет задачи 3 в теле ответа сервера");

        int oneTaskIndex = bodyPrioritized.indexOf(gson.toJson(task2));
        int twoTaskIndex = bodyPrioritized.indexOf(gson.toJson(task1));
        int threeTaskIndex = bodyPrioritized.indexOf(gson.toJson(task3));

        assertTrue(oneTaskIndex < twoTaskIndex && twoTaskIndex < threeTaskIndex, "Последовательность задач нарушена");
    }

}