package coffeeshop.container;

import coffeeshop.order.InMemoryOrderRepository;
import coffeeshop.order.OrderRepository;
import coffeeshop.order.OrderService;

/**
 * Demonstrates the custom DI container wiring the same classes that Spring wires.
 * Run this directly: it doesn't need Spring Boot.
 * <p>
 * java -cp target/classes coffeeshop.container.ContainerDemo
 */
public class ContainerDemo {

    public static void main(String[] args) {
        var container = new Container();

        // Register: interface -> implementation
        container.register(OrderRepository.class, InMemoryOrderRepository.class);

        // Resolve: the container reads OrderService's constructor,
        // sees it needs OrderRepository, resolves that first, then creates OrderService.
        var service = container.resolve(OrderService.class);

        System.out.println("Orders from custom DI container:");
        service.listOrders().forEach(order ->
            System.out.printf("  #%d %s (%s)%n", order.id(), order.drink(), order.status())
        );
    }
}
