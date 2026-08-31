import {MiddlewareConsumer, Module, NestModule} from "@nestjs/common";
import {OrderModule} from "./order/order.module";
import {MenuModule} from "./menu/menu.module";
import {RequestIdMiddleware} from "./middleware/request-id.middleware";
import {LoggingMiddleware} from "./middleware/logging.middleware";

@Module({
    imports: [OrderModule, MenuModule],
})
export class AppModule implements NestModule {
    // NestJS middleware is registered here, per route pattern.
    // Order matters: RequestId first, then Logging.
    configure(consumer: MiddlewareConsumer) {
        consumer.apply(RequestIdMiddleware, LoggingMiddleware).forRoutes("*");
    }
}
