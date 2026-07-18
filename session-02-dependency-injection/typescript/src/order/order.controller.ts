import {Controller, Get, NotFoundException, Param, ParseIntPipe} from "@nestjs/common";
import {Order} from "./order.model";
import {OrderService} from "./order.service";

@Controller("orders")
export class OrderController {
    // The controller depends on OrderService, not OrderRepository.
    // Each layer declares only what it needs. NestJS wires the chain.
    constructor(private readonly orderService: OrderService) {
    }

    @Get()
    listOrders(): Order[] {
        return this.orderService.listOrders();
    }

    @Get(":id")
    getOrder(@Param("id", ParseIntPipe) id: number): Order {
        const order = this.orderService.findOrder(id);
        if (!order) {
            throw new NotFoundException(`Order ${id} not found`);
        }
        return order;
    }
}
