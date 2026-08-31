import {Controller, Get, NotFoundException, Param, Post} from "@nestjs/common";
import {OrderService} from "./order.service";

@Controller("orders")
export class OrderController {
    constructor(private readonly orderService: OrderService) {
    }

    @Get()
    listOrders() {
        return this.orderService.listOrders();
    }

    @Get(":id")
    getOrder(@Param("id") id: string) {
        const order = this.orderService.findOrder(parseInt(id));
        if (!order) throw new NotFoundException("Order not found");
        return order;
    }

    @Post(":id/prepare")
    prepareOrder(@Param("id") id: string) {
        const order = this.orderService.findOrder(parseInt(id));
        if (!order) throw new NotFoundException("Order not found");
        return this.orderService.placeOrder(order.id, order.drink);
    }
}
