import {Module} from "@nestjs/common";
import {OrderModule} from "./order/order.module";
import {MenuModule} from "./menu/menu.module";

@Module({
    imports: [OrderModule, MenuModule],
})
export class AppModule {
}
