/**
 * Demonstrates the custom DI container wiring the same classes NestJS wires.
 * Run: npm run container:demo
 */
import "reflect-metadata";
import {Container} from "./container";
import {InMemoryOrderRepository, OrderRepository} from "../order/order.repository";
import {OrderService} from "../order/order.service";

const container = new Container();

// Register: abstract class -> implementation
container.register(OrderRepository, InMemoryOrderRepository);

// Resolve: the container reads OrderService's constructor metadata,
// sees it needs OrderRepository, resolves that first, then creates OrderService.
const service = container.resolve<OrderService>(OrderService);

console.log("Orders from custom DI container:");
for (const order of service.listOrders()) {
    console.log(`  #${order.id} ${order.drink} (${order.status})`);
}
