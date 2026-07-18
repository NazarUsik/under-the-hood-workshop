import {Module} from "@nestjs/common";
import {OrderController} from "./order.controller";
import {OrderService} from "./order.service";
import {InMemoryOrderRepository, OrderRepository} from "./order.repository";

@Module({
    controllers: [OrderController],
    providers: [
        OrderService,
        // This is NestJS DI: provide OrderRepository, use InMemoryOrderRepository as the implementation.
        // To swap implementations, just change useClass here. No other code changes needed.
        {provide: OrderRepository, useClass: InMemoryOrderRepository},
    ],
})
export class OrderModule {
}
