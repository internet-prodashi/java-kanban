import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpSubtasksController extends BaseHttpHandler {

    public HttpSubtasksController(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String requestMethod = exchange.getRequestMethod();

        Endpoint endpoint = getEndpoint(pathParts, requestMethod);
        switch (endpoint) {
            case GET:
                sendText(exchange, gson.toJson(taskManager.getAllSubtasks()));
            case GET_ID:
                sendText(exchange, gson.toJson(taskManager.getSubtaskByID(Integer.parseInt(pathParts[2]))));
            case POST: {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Subtask subtask = gson.fromJson(body, Subtask.class);
                taskManager.addSubtask(subtask.getEpicId(), subtask);
                sendSuccessfullyModified(exchange, "Подзадача успешно добавлена");
                break;
            }
            case POST_ID: {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                taskManager.updateSubtaskByID(gson.fromJson(body, Subtask.class));
                sendSuccessfullyModified(exchange, "Подзадача успешно обнолена");
                break;
            }
            case DELETE: {
                taskManager.deleteAllSubtasks();
                sendText(exchange, "Все подзадачи удалены");
                break;
            }
            case DELETE_ID: {
                taskManager.deleteSubtaskByID(Integer.parseInt(pathParts[2]));
                sendText(exchange, "Подзадача с идентификатором " + Integer.parseInt(pathParts[2]) + " удалена");
                break;
            }
            default:
                throw new RuntimeException("Эндпоинт не найден: " + requestMethod + " " + exchange.getRequestURI().getPath());
        }
    }

}
