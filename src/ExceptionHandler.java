import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class ExceptionHandler extends Filter {
    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        try {
            chain.doFilter(exchange);
        } catch (NotFoundException e) {
            BaseHttpHandler.sendNotFound(exchange, e.getMessage());
        } catch (TaskOverlapsException e) {
            BaseHttpHandler.sendHasInteractions(exchange, e.getMessage());
        } catch (Exception e) {
            String message = "Внутренняя ошибка сервера: " + e.getClass().getSimpleName();
            BaseHttpHandler.sendIntervalServerError(exchange, message);
            e.printStackTrace();
        }
    }

    @Override
    public String description() {
        return "Обработчик исключений";
    }

}
