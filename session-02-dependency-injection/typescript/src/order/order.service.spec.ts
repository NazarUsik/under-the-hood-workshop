import {OrderService} from "./order.service";
import {Order} from "./order.model";
import {OrderRepository} from "./order.repository";

// Exercise 4: Unit test with a stub. No NestJS testing module needed.
class StubOrderRepository extends OrderRepository {
    private readonly orders: Order[] = [
        {id: 1, drink: "Test Latte", status: "ready"},
        {id: 2, drink: "Test Espresso", status: "preparing"},
    ];

    findAll(): Order[] {
        return this.orders;
    }

    findById(id: number): Order | undefined {
        return this.orders.find((o) => o.id === id);
    }
}

// DI by hand: pass the stub to the constructor.
const service = new OrderService(new StubOrderRepository());

describe("OrderService", () => {
    it("should list all orders", () => {
        const orders = service.listOrders();
        expect(orders).toHaveLength(2);
        expect(orders[0].drink).toBe("Test Latte");
    });

    it("should find order by id", () => {
        const order = service.findOrder(2);
        expect(order).toBeDefined();
        expect(order!.drink).toBe("Test Espresso");
    });

    it("should return undefined for missing id", () => {
        const order = service.findOrder(99);
        expect(order).toBeUndefined();
    });
});
