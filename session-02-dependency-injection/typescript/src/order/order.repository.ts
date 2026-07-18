import {Injectable} from "@nestjs/common";
import {Order} from "./order.model";

// TypeScript interfaces don't exist at runtime, so NestJS can't use them as injection tokens.
// We use an abstract class instead: it serves as both a type and a runtime token.
@Injectable()
export abstract class OrderRepository {
    abstract findAll(): Order[];

    abstract findById(id: number): Order | undefined;
}

@Injectable()
export class InMemoryOrderRepository extends OrderRepository {
    private readonly orders: Order[] = [
        {id: 1, drink: "Latte", status: "ready"},
        {id: 2, drink: "Espresso", status: "preparing"},
        {id: 3, drink: "Cappuccino", status: "pending"},
    ];

    findAll(): Order[] {
        return this.orders;
    }

    findById(id: number): Order | undefined {
        return this.orders.find((o) => o.id === id);
    }
}
