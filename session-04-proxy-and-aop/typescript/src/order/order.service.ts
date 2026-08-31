import {Injectable} from "@nestjs/common";
import {Order} from "./order.model";
import {OrderRepository} from "./order.repository";

// Notice: NO logging, NO timing code here. Interceptors add it.
@Injectable()
export class OrderService {
    constructor(private readonly repository: OrderRepository) {
    }

    listOrders(): Order[] {
        return this.repository.findAll();
    }

    findOrder(id: number): Order | undefined {
        return this.repository.findById(id);
    }
}
