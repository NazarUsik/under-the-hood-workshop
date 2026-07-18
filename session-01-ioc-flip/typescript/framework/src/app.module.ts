import {MiddlewareConsumer, Module, NestModule} from "@nestjs/common";
import {OrderController} from "./order/order.controller";
import {MenuController} from "./menu/menu.controller";
import {LoggingMiddleware} from "./logging.middleware";

@Module({
    controllers: [OrderController, MenuController],
})
export class AppModule implements NestModule {
    configure(consumer: MiddlewareConsumer) {
        consumer.apply(LoggingMiddleware).forRoutes("*");
    }
}
