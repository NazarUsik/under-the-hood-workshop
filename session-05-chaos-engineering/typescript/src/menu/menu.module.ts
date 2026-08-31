import {Module} from "@nestjs/common";
import {MenuController} from "./menu.controller";
import {MenuService} from "./menu.service";
import {InMemoryMenuRepository, MenuRepository} from "./menu.repository";

@Module({
    controllers: [MenuController],
    providers: [
        MenuService,
        {provide: MenuRepository, useClass: InMemoryMenuRepository},
    ],
})
export class MenuModule {
}
