import {Controller, Get, NotFoundException, Param} from "@nestjs/common";
import {Order} from "./order.model";
import {OrderService} from "./order.service";

@Controller("orders")
export class OrderController {
    constructor(private readonly orderService: OrderService) {
    }

    @Get()
    listOrders(): Order[] {
        return this.orderService.listOrders();
    }

    @Get(":id")
    getOrder(@Param("id") id: string): Order {
        const order = this.orderService.findOrder(parseInt(id));
        if (!order) {
            throw new NotFoundException("Order not found");
        }
        return order;
    }
}
