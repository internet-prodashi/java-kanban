import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpTasksController extends BaseHttpHandler {

    public HttpTasksController(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String requestMethod = exchange.getRequestMethod();

        Endpoint endpoint = getEndpoint(pathParts, requestMethod);
        switch (endpoint) {
            case GET:
                sendText(exchange, gson.toJson(taskManager.getAllTasks()));
            case GET_ID:
                sendText(exchange, gson.toJson(taskManager.getTaskByID(Integer.parseInt(pathParts[2]))));
            case POST: {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                taskManager.addTask(gson.fromJson(body, Task.class));
                sendSuccessfullyModified(exchange, "Задача успешно добавлена");
                break;
            }
            case POST_ID: {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                taskManager.updateTaskByID(gson.fromJson(body, Task.class));
                sendSuccessfullyModified(exchange, "Задача успешно обнолена");
                break;
            }
            case DELETE: {
                taskManager.deleteAllTask();
                sendText(exchange, "Все задачи удалены");
                break;
            }
            case DELETE_ID: {
                taskManager.deleteTaskByID(Integer.parseInt(pathParts[2]));
                sendText(exchange, "Задача с идентификатором " + Integer.parseInt(pathParts[2]) + " удалена");
                break;
            }
            default:
                throw new RuntimeException("Эндпоинт не найден: " + requestMethod + " " + exchange.getRequestURI().getPath());
        }
    }

}