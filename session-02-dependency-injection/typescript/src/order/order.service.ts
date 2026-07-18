import {Injectable} from "@nestjs/common";
import {Order} from "./order.model";
import {OrderRepository} from "./order.repository";

@Injectable()
export class OrderService {
    // Constructor injection: NestJS reads the type metadata (OrderRepository)
    // and injects the matching provider from the module.
    constructor(private readonly repository: OrderRepository) {
    }

    listOrders(): Order[] {
        return this.repository.findAll();
    }

    findOrder(id: number): Order | undefined {
        return this.repository.findById(id);
    }
}
