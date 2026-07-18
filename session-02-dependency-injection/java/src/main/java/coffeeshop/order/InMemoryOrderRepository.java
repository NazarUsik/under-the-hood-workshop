package coffeeshop.order;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final List<Order> orders = List.of(
            new Order(1, "Latte", "ready"),
            new Order(2, "Espresso", "preparing"),
            new Order(3, "Cappuccino", "pending")
    );

    @Override
    public List<Order> findAll() {
        return orders;
    }

    @Override
    public Optional<Order> findById(int id) {
        return orders.stream()
                .filter(order -> order.id() == id)
                .findFirst();
    }
}
