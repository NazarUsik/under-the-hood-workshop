import {Module} from "@nestjs/common";
import {OrderController} from "./order.controller";
import {OrderService} from "./order.service";
import {OrderRepository} from "./order.repository";
import {PostgresOrderRepository} from "./postgres-order.repository";

@Module({
    controllers: [OrderController],
    providers: [
        OrderService,
        // Exercise 3: Swapped to PostgresOrderRepository. Just changed useClass, nothing else.
        {provide: OrderRepository, useClass: PostgresOrderRepository},
    ],
})
export class OrderModule {
}
