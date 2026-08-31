package coffeeshop.order;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final coffeeshop.kitchen.KitchenService kitchenService;

    public OrderService(OrderRepository repository, coffeeshop.kitchen.KitchenService kitchenService) {
        this.repository = repository;
        this.kitchenService = kitchenService;
    }

    public List<Order> listOrders() {
        return repository.findAll();
    }

    public Optional<Order> findOrder(int id) {
        return repository.findById(id);
    }

    public coffeeshop.kitchen.Preparation placeOrder(int orderId, String drink) {
        return kitchenService.prepare(orderId, drink);
    }
}
