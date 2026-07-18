package coffeeshop;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderController {

    @GetMapping("/orders")
    public List<Order> listOrders() {
        // Spring calls this method when GET /orders arrives.
        // You never invoke it directly. That's IoC.
        return List.of(
            new Order(1, "Latte"),
            new Order(2, "Espresso"),
            new Order(3, "Cappuccino")
        );
    }

    // Exercise 2: GET /menu - just one annotation and a return statement.
    // Compare this to the library version.
    @GetMapping("/menu")
    public List<MenuItem> listMenu() {
        return List.of(
            new MenuItem(1, "Latte", 4.50),
            new MenuItem(2, "Espresso", 3.00),
            new MenuItem(3, "Cappuccino", 4.00),
            new MenuItem(4, "Americano", 3.50)
        );
    }

    // Exercise 4: Throw an exception. Spring returns a clean 500 JSON error
    // with timestamp, status, error, and path. No crash.
    @GetMapping("/error")
    public String simulateError() {
        throw new RuntimeException("Something went wrong!");
    }
}
