import {Injectable, OnModuleDestroy, OnModuleInit} from "@nestjs/common";
import {Order} from "./order.model";
import {OrderRepository} from "./order.repository";

// OnModuleInit and OnModuleDestroy are NestJS lifecycle hooks.
// The framework calls these at startup and shutdown.
@Injectable()
export class OrderService implements OnModuleInit, OnModuleDestroy {
    constructor(private readonly repository: OrderRepository) {
    }

    onModuleInit() {
        console.log("[Lifecycle] OrderService initialized");
    }

    onModuleDestroy() {
        console.log("[Lifecycle] OrderService shutting down");
    }

    listOrders(): Order[] {
        return this.repository.findAll();
    }

    findOrder(id: number): Order | undefined {
        return this.repository.findById(id);
    }
}
