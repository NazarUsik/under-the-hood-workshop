package coffeeshop.order;

import coffeeshop.kitchen.KitchenService;
import coffeeshop.kitchen.Preparation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// No logging, no timing, no audit, no tracing code here.
// All cross-cutting concerns are added by aspects.
@Service
public class OrderService {

    private final OrderRepository repository;
    private final KitchenService kitchenService;

    public OrderService(OrderRepository repository, KitchenService kitchenService) {
        this.repository = repository;
        this.kitchenService = kitchenService;
    }

    public List<Order> listOrders() {
        return repository.findAll();
    }

    public Optional<Order> findOrder(int id) {
        return repository.findById(id);
    }

    public Preparation placeOrder(int orderId, String drink) {
        return kitchenService.prepare(orderId, drink);
    }
}
