import {Module} from "@nestjs/common";
import {OrderController} from "./order.controller";
import {OrderService} from "./order.service";
import {InMemoryOrderRepository, OrderRepository} from "./order.repository";
import {KitchenModule} from "../kitchen/kitchen.module";

@Module({
    imports: [KitchenModule],
    controllers: [OrderController],
    providers: [
        OrderService,
        {provide: OrderRepository, useClass: InMemoryOrderRepository},
    ],
})
export class OrderModule {
}
