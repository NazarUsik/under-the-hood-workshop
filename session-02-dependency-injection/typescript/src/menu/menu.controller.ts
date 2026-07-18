import {Controller, Get} from "@nestjs/common";
import {MenuItem} from "./menu.model";
import {MenuService} from "./menu.service";

@Controller("menu")
export class MenuController {
    constructor(private readonly menuService: MenuService) {
    }

    @Get()
    listMenu(): MenuItem[] {
        return this.menuService.listItems();
    }
}
