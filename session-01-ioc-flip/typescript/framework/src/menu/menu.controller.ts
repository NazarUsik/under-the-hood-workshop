import {Controller, Get} from "@nestjs/common";
import {MenuItem} from "./menu-item.model";

// Exercise 2: GET /menu - one decorator, one class, one method.
// Compare this to the library version.
@Controller("menu")
export class MenuController {
    @Get()
    listMenu(): MenuItem[] {
        return [
            {id: 1, name: "Latte", price: 4.50},
            {id: 2, name: "Espresso", price: 3.00},
            {id: 3, name: "Cappuccino", price: 4.00},
            {id: 4, name: "Americano", price: 3.50},
        ];
    }
}
