package coffeeshop.order;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Notice: NO logging, NO timing, NO audit code here.
// All cross-cutting concerns are added by proxies/aspects.
@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public List<Order> listOrders() {
        return repository.findAll();
    }

    public Optional<Order> findOrder(int id) {
        return repository.findById(id);
    }
}
