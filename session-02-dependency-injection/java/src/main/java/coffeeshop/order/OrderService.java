package coffeeshop.order;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    // Constructor injection: Spring sees this constructor, finds an OrderRepository
    // bean in the registry, and passes it here. No @Autowired needed (since Spring 4.3).
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
