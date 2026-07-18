package coffeeshop.order;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Exercise 4: Unit test with a stub repository.
// No Spring context, no database, no HTTP. Just constructor injection and assertions.
class OrderServiceTest {

    // A simple stub: no framework mocking library needed.
    private final OrderRepository stubRepo = new OrderRepository() {
        private final List<Order> orders = List.of(
                new Order(1, "Test Latte", "ready"),
                new Order(2, "Test Espresso", "preparing")
        );

        @Override
        public List<Order> findAll() {
            return orders;
        }

        @Override
        public Optional<Order> findById(int id) {
            return orders.stream()
                    .filter(o -> o.id() == id)
                    .findFirst();
        }
    };

    // DI by hand: pass the stub to the constructor.
    private final OrderService service = new OrderService(stubRepo);

    @Test
    void listOrders_returnsAllOrders() {
        List<Order> orders = service.listOrders();
        assertEquals(2, orders.size());
        assertEquals("Test Latte", orders.getFirst().drink());
    }

    @Test
    void findOrder_returnsMatchingOrder() {
        Optional<Order> order = service.findOrder(2);
        assertTrue(order.isPresent());
        assertEquals("Test Espresso", order.get().drink());
    }

    @Test
    void findOrder_returnsEmptyForMissingId() {
        Optional<Order> order = service.findOrder(99);
        assertTrue(order.isEmpty());
    }
}
