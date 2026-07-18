import {Injectable} from "@nestjs/common";
import {Order} from "./order.model";
import {OrderRepository} from "./order.repository";

// Exercise 3: Swap implementation. Change useClass in order.module.ts to use this.
@Injectable()
export class PostgresOrderRepository extends OrderRepository {
    private readonly orders: Order[] = [
        {id: 1, drink: "Flat White", status: "ready"},
        {id: 2, drink: "Mocha", status: "preparing"},
        {id: 3, drink: "Cold Brew", status: "pending"},
        {id: 4, drink: "Matcha Latte", status: "ready"},
    ];

    findAll(): Order[] {
        return this.orders;
    }

    findById(id: number): Order | undefined {
        return this.orders.find((o) => o.id === id);
    }
}
