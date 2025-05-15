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

public class HttpTaskServerEpicsTest {
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

        String epic1Json = gson.toJson(epic1);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        assertEquals(200, response.statusCode(), "Неверный код ответа сервера");
        assertTrue(responseBody.contains(epic1Json), "Нет добавленного эпика в теле ответа сервера");

        taskManager.deleteAllEpic();

        HttpResponse<String> responseNew = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBodyNew = responseNew.body();

        assertEquals(200, responseNew.statusCode(), "Неверный код ответа сервера после удаления всех эпиков");
        assertEquals("[]", responseBodyNew, "Тело ответа сервера не пустое после удаления всех эпиков");
    }

    @Test
    void testReturnEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic(1, "Заголовок эпика 1", "Подробное описание эпика 1", Status.NEW);
        taskManager.addEpic(epic1);

        String epic1Json = gson.toJson(epic1);

        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        assertEquals(200, response.statusCode(), "Неверный код ответа сервера");
        assertTrue(responseBody.contains(epic1Json), "Нет добавленного эпика в теле ответа сервера");

        taskManager.deleteAllEpic();

        HttpResponse<String> responseNew = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBodyNew = responseNew.body();

        assertEquals(404, responseNew.statusCode(), "Неверный код ответа сервера после удаления всех эпиков");
        assertEquals("Эпик с идентификатором 1 не найден", responseBodyNew, "Тело ответа сервера не пустое после удаления всех эпиков");
    }

    @Test
    void testReturnSubtasksForEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic(1, "Заголовок эпика 1", "Подробное описание эпика 1", Status.NEW);
        taskManager.addEpic(epic1);

        LocalDateTime expectedStartTime1 = LocalDateTime.of(2025, 4, 27, 23, 7);
        Duration expectedDuration1 = Duration.ofMinutes(300);
        Subtask subtask1 = new Subtask(2, "Подзадача 1 эпика 1", "описание подзадачи 1 эпика 1", 1, expectedStartTime1, expectedDuration1);
        taskManager.addSubtask(1, subtask1);

        LocalDateTime expectedStartTime2 = LocalDateTime.of(2025, 4, 28, 23, 7);
        Duration expectedDuration2 = Duration.ofMinutes(300);
        Subtask subtask2 = new Subtask(3, "Подзадача 2 эпика 1", "описание подзадачи 2 эпика 1", 1, expectedStartTime2, expectedDuration2);
        taskManager.addSubtask(1, subtask2);

        String subtask1Json = gson.toJson(subtask1);
        String subtask2Json = gson.toJson(subtask2);

        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        assertEquals(200, response.statusCode(), "Неверный код ответа сервера");
        assertTrue(responseBody.contains(subtask1Json), "Нет добавленной подзадачи 1 в теле ответа сервера");
        assertTrue(responseBody.contains(subtask2Json), "Нет добавленной подзадачи 2 в теле ответа сервера");

        taskManager.deleteAllEpic();

        HttpResponse<String> responseNew = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBodyNew = responseNew.body();

        assertEquals(404, responseNew.statusCode(), "Неверный код ответа сервера после удаления всех эпиков");
        assertEquals("Эпик с идентификатором 1 не найден", responseBodyNew, "Тело ответа сервера не пустое после удаления всех эпиков");
    }

    @Test
    void testAddEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic(1, "Заголовок эпика 1", "Подробное описание эпика 1", Status.NEW);

        String epic1Json = gson.toJson(epic1);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epic1Json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный код ответа сервера");

        List<Epic> tasksFromManager = taskManager.getAllEpics();

        assertNotNull(tasksFromManager, "Эпики не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Заголовок эпика 1", tasksFromManager.getFirst().getTitle(), "Некорректное имя эпика");
    }

    @Test
    void testUpdateEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic(1, "Заголовок эпика 1", "Подробное описание эпика 1", Status.NEW);
        taskManager.addEpic(epic1);

        Epic epic1Update = new Epic(1, "Заголовок эпика 1 изменен", "Подробное описание эпика 1 изменено", Status.NEW);

        String epic1UpdateJson = gson.toJson(epic1Update);

        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epic1UpdateJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный код ответа сервера");

        List<Epic> tasksFromManager = taskManager.getAllEpics();

        assertNotNull(tasksFromManager, "Эпики не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Заголовок эпика 1 изменен", tasksFromManager.getFirst().getTitle(), "Имя эпика не обновилось");
        assertEquals("Подробное описание эпика 1 изменено", tasksFromManager.getFirst().getDescription(), "Описание эпика не обновилось");

        Epic epic2Update = new Epic(2, "Заголовок эпика 1 изменен", "Подробное описание эпика 1 изменено", Status.NEW);

        String epic2UpdateJson = gson.toJson(epic2Update);

        URI urlNew = URI.create("http://localhost:8080/epics/2");
        HttpRequest requestNew = HttpRequest
                .newBuilder()
                .uri(urlNew)
                .POST(HttpRequest.BodyPublishers.ofString(epic2UpdateJson))
                .build();

        HttpResponse<String> responseNew = httpClient.send(requestNew, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseNew.statusCode(), "Неверный код ответа сервера");
    }

    @Test
    void testDeleteAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic(1, "Заголовок эпика 1", "Подробное описание эпика 1", Status.NEW);
        taskManager.addEpic(epic1);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный код ответа сервера");
        assertTrue(taskManager.getAllEpics().isEmpty(), "Не все эпики удалены");
    }

    @Test
    void testDeleteEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic(1, "Заголовок эпика 1", "Подробное описание эпика 1", Status.NEW);
        taskManager.addEpic(epic1);

        URI url = URI.create("http://localhost:8080/epics/2");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Неверный код ответа сервера");

        URI urlNew = URI.create("http://localhost:8080/epics/1");
        HttpRequest requestNew = HttpRequest
                .newBuilder()
                .uri(urlNew)
                .DELETE()
                .build();

        HttpResponse<String> responseNew = httpClient.send(requestNew, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseNew.statusCode(), "Неверный код ответа сервера");
        assertTrue(taskManager.getAllEpics().isEmpty(), "Эпик 1 не удален");
    }

}
