import {Injectable} from "@nestjs/common";
import {Order} from "./order.model";
import {OrderRepository} from "./order.repository";
import {KitchenService, Preparation} from "../kitchen/kitchen.service";

@Injectable()
export class OrderService {
    constructor(
        private readonly repository: OrderRepository,
        private readonly kitchenService: KitchenService,
    ) {
    }

    listOrders(): Order[] {
        return this.repository.findAll();
    }

    findOrder(id: number): Order | undefined {
        return this.repository.findById(id);
    }

    placeOrder(orderId: number, drink: string): Preparation {
        return this.kitchenService.prepare(orderId, drink);
    }
}
