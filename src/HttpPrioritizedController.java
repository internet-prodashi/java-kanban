import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class HttpPrioritizedController extends BaseHttpHandler {

    public HttpPrioritizedController(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String requestMethod = exchange.getRequestMethod();

        Endpoint endpoint = getEndpoint(pathParts, requestMethod);
        switch (endpoint) {
            case GET:
                sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()));
            default:
                throw new RuntimeException("Эндпоинт не найден: " + requestMethod + " " + exchange.getRequestURI().getPath());
        }
    }

}
