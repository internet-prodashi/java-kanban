import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpEpicsController extends BaseHttpHandler {

    public HttpEpicsController(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String requestMethod = exchange.getRequestMethod();

        Endpoint endpoint = getEndpoint(pathParts, requestMethod);
        switch (endpoint) {
            case GET:
                sendText(exchange, gson.toJson(taskManager.getAllEpics()));
            case GET_ID:
                sendText(exchange, gson.toJson(taskManager.getEpicByID(Integer.parseInt(pathParts[2]))));
            case GET_ID_EPIC_SUBTASKS:
                sendText(exchange, gson.toJson(taskManager.getSubtaskListByEpicId(Integer.parseInt(pathParts[2]))));
            case POST: {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                taskManager.addEpic(gson.fromJson(body, Epic.class));
                sendSuccessfullyModified(exchange, "Эпик успешно добавлен");
                break;
            }
            case POST_ID: {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                taskManager.updateEpicByID(gson.fromJson(body, Epic.class));
                sendSuccessfullyModified(exchange, "Эпик успешно обнолен");
                break;
            }
            case DELETE: {
                taskManager.deleteAllEpic();
                sendText(exchange, "Все эпики удалены");
                break;
            }
            case DELETE_ID: {
                taskManager.deleteEpicByID(Integer.parseInt(pathParts[2]));
                sendText(exchange, "Эпик с идентификатором " + Integer.parseInt(pathParts[2]) + " удален");
                break;
            }
            default:
                throw new RuntimeException("Эндпоинт не найден: " + requestMethod + " " + exchange.getRequestURI().getPath());
        }
    }

}
