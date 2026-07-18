import {Injectable} from "@nestjs/common";
import {MenuItem} from "./menu.model";

@Injectable()
export abstract class MenuRepository {
    abstract findAll(): MenuItem[];
}

@Injectable()
export class InMemoryMenuRepository extends MenuRepository {
    private readonly items: MenuItem[] = [
        {id: 1, name: "Latte", price: 4.5},
        {id: 2, name: "Espresso", price: 3.0},
        {id: 3, name: "Cappuccino", price: 4.0},
        {id: 4, name: "Americano", price: 3.5},
    ];

    findAll(): MenuItem[] {
        return this.items;
    }
}
