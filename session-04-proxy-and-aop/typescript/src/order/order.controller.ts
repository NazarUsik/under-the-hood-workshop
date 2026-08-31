import {Controller, Get, NotFoundException, Param, UseInterceptors} from "@nestjs/common";
import {Order} from "./order.model";
import {OrderService} from "./order.service";
import {LoggingInterceptor} from "../proxy/logging.interceptor";
import {TimingInterceptor} from "../proxy/timing.interceptor";
import {AuditInterceptor} from "../proxy/audit.interceptor";

// Interceptors stack like proxies: Logging -> Timing -> Audit -> handler
@Controller("orders")
@UseInterceptors(LoggingInterceptor, TimingInterceptor, AuditInterceptor)
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
