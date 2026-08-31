import {MiddlewareConsumer, Module, NestModule} from "@nestjs/common";
import {OrderModule} from "./order/order.module";
import {MenuModule} from "./menu/menu.module";
import {LoggingMiddleware} from "./middleware/logging.middleware";

@Module({
    imports: [OrderModule, MenuModule],
})
export class AppModule implements NestModule {
    // NestJS middleware is registered here, per route pattern.
    // This is where you control the middleware pipeline order.
    configure(consumer: MiddlewareConsumer) {
        consumer.apply(LoggingMiddleware).forRoutes("*");
    }
}
