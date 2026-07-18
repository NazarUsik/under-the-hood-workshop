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
}
