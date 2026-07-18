import {Injectable} from "@nestjs/common";
import {MenuItem} from "./menu.model";
import {MenuRepository} from "./menu.repository";

@Injectable()
export class MenuService {
    constructor(private readonly repository: MenuRepository) {
    }

    listItems(): MenuItem[] {
        return this.repository.findAll();
    }
}
