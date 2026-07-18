import {Module} from "@nestjs/common";
import {OrderController} from "./order/order.controller";

@Module({
    controllers: [OrderController],
})
export class AppModule {
}
