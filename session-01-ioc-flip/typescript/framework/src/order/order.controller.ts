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

    // Exercise 4: Throw an error. NestJS returns a clean JSON error response
    // with {"statusCode": 500, "message": "Internal server error"}. No crash.
    @Get("error")
    simulateError(): string {
        throw new Error("Something went wrong!");
    }
}
