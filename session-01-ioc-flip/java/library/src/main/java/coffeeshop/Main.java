package coffeeshop;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException {
        var gson = new Gson();
        var server = HttpServer.create(new InetSocketAddress(8080), 0);

        // YOU define the route. YOU handle the request. YOU write the response.
        server.createContext("/orders", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                var orders = List.of(
                    new Order(1, "Latte"),
                    new Order(2, "Espresso"),
                    new Order(3, "Cappuccino")
                );

                var json = gson.toJson(orders);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, json.getBytes().length);
                try (var os = exchange.getResponseBody()) {
                    os.write(json.getBytes());
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
            exchange.close();
        });

        // Exercise 2: GET /menu - notice how much manual work this takes
        server.createContext("/menu", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                var menu = List.of(
                    new MenuItem(1, "Latte", 4.50),
                    new MenuItem(2, "Espresso", 3.00),
                    new MenuItem(3, "Cappuccino", 4.00),
                    new MenuItem(4, "Americano", 3.50)
                );

                var json = gson.toJson(menu);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, json.getBytes().length);
                try (var os = exchange.getResponseBody()) {
                    os.write(json.getBytes());
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
            exchange.close();
        });

        // Exercise 4: An endpoint that throws an error
        server.createContext("/error", exchange -> {
            // In library mode, an unhandled exception crashes the handler.
            // The server stays up, but the client gets no response or a broken one.
            throw new RuntimeException("Something went wrong!");
        });

        // Exercise 3: Request logging - you have to add it manually to every handler,
        // or wrap the entire server with a filter.
        server.createContext("/").getFilters().add(new com.sun.net.httpserver.Filter() {
            @Override
            public String description() { return "Request Logger"; }

            @Override
            public void doFilter(com.sun.net.httpserver.HttpExchange exchange,
                                 com.sun.net.httpserver.Filter.Chain chain) throws IOException {
                System.out.printf("[%s] %s %s%n", LocalDateTime.now(),
                    exchange.getRequestMethod(), exchange.getRequestURI());
                chain.doFilter(exchange);
            }
        });

        // YOU start the server. YOU own the loop.
        server.start();
        System.out.println("Library server running on http://localhost:8080");
        System.out.println("Try: curl http://localhost:8080/orders");
    }
}
