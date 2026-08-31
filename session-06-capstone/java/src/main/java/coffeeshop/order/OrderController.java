package coffeeshop.order;

import coffeeshop.kitchen.Preparation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> listOrders() {
        return orderService.listOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable int id) {
        return orderService.findOrder(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/prepare")
    public ResponseEntity<Preparation> prepareOrder(@PathVariable int id) {
        return orderService.findOrder(id)
            .map(order -> ResponseEntity.ok(orderService.placeOrder(order.id(), order.drink())))
            .orElse(ResponseEntity.notFound().build());
    }
}
