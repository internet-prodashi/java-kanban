import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    protected BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    public static void sendText(HttpExchange h, String text) throws IOException {
        send(h, text, 200);
    }

    public static void sendSuccessfullyModified(HttpExchange h, String text) throws IOException {
        send(h, text, 201);
    }

    public static void sendNotFound(HttpExchange h, String text) throws IOException {
        send(h, text, 404);
    }

    public static void sendHasInteractions(HttpExchange h, String text) throws IOException {
        send(h, text, 406);
    }

    public static void sendIntervalServerError(HttpExchange h, String text) throws IOException {
        send(h, text, 500);
    }

    public Endpoint getEndpoint(String[] pathParts, String requestMethod) {
        if (pathParts[1].equals("tasks") || pathParts[1].equals("subtasks") || pathParts[1].equals("epics") ||
                pathParts[1].equals("history") || pathParts[1].equals("prioritized")) {
            switch (requestMethod) {
                case "GET": {
                    if (pathParts.length == 2) {
                        return Endpoint.GET;
                    }
                    if (pathParts.length == 3 && isInteger(pathParts[2])) {
                        return Endpoint.GET_ID;
                    }
                    if (pathParts.length == 4 && pathParts[1].equals("epics") &&
                            isInteger(pathParts[2]) && pathParts[3].equals("subtasks")) {
                        return Endpoint.GET_ID_EPIC_SUBTASKS;
                    }
                    return Endpoint.UNKNOWN;
                }
                case "POST": {
                    if (pathParts.length == 2) {
                        return Endpoint.POST;
                    }
                    if (pathParts.length == 3 && isInteger(pathParts[2])) {
                        return Endpoint.POST_ID;
                    }
                    return Endpoint.UNKNOWN;
                }
                case "DELETE": {
                    if (pathParts.length == 2) {
                        return Endpoint.DELETE;
                    }
                    if (pathParts.length == 3 && isInteger(pathParts[2])) {
                        return Endpoint.DELETE_ID;
                    }
                    return Endpoint.UNKNOWN;
                }
                default:
                    return Endpoint.UNKNOWN;
            }
        }
        return Endpoint.UNKNOWN;
    }

    private static void send(HttpExchange h, String text, int code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}