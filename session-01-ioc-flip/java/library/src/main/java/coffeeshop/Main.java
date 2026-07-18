package coffeeshop;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

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

        // YOU start the server. YOU own the loop.
        server.start();
        System.out.println("Library server running on http://localhost:8080");
        System.out.println("Try: curl http://localhost:8080/orders");
    }
}
