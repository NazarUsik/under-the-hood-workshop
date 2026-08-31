import {Controller, Get, UseInterceptors} from "@nestjs/common";
import {MenuItem} from "./menu.model";
import {MenuService} from "./menu.service";
import {LoggingInterceptor} from "../proxy/logging.interceptor";
import {TimingInterceptor} from "../proxy/timing.interceptor";

@Controller("menu")
@UseInterceptors(LoggingInterceptor, TimingInterceptor)
export class MenuController {
    constructor(private readonly menuService: MenuService) {
    }

    @Get()
    listMenu(): MenuItem[] {
        return this.menuService.listItems();
    }
}
