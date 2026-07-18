package coffeeshop.order;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Exercise 3: Swap the repository.
// @Primary makes this the default OrderRepository bean.
// Spring picks this over InMemoryOrderRepository without changing OrderService.
@Repository
@Primary
public class PostgresOrderRepository implements OrderRepository {

    private final List<Order> orders = List.of(
            new Order(1, "Flat White", "ready"),
            new Order(2, "Mocha", "preparing"),
            new Order(3, "Cold Brew", "pending"),
            new Order(4, "Matcha Latte", "ready")
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
