import java.io.IOException;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private final HttpServer httpServer;
    private final TaskManager taskManager;
    private final Gson gson;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());
        httpTaskServer.start();
    }

    public void start() {

        Filter filter = new ExceptionHandler();
        httpServer.createContext("/tasks", new HttpTasksController(taskManager, gson)).getFilters().add(filter);
        httpServer.createContext("/subtasks", new HttpSubtasksController(taskManager, gson)).getFilters().add(filter);
        httpServer.createContext("/epics", new HttpEpicsController(taskManager, gson)).getFilters().add(filter);
        httpServer.createContext("/history", new HttpHistoryController(taskManager, gson)).getFilters().add(filter);
        httpServer.createContext("/prioritized", new HttpPrioritizedController(taskManager, gson)).getFilters().add(filter);

        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    public Gson getGson() {
        return gson;
    }

}
