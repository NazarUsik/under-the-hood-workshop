import {Controller, Get} from "@nestjs/common";
import {Order} from "./order.model";

@Controller("orders")
export class OrderController {
    @Get()
    listOrders(): Order[] {
        // NestJS calls this when GET /orders arrives.
        // You never invoke it directly. That's IoC.
        return [
            {id: 1, drink: "Latte"},
            {id: 2, drink: "Espresso"},
            {id: 3, drink: "Cappuccino"},
        ];
    }
}
