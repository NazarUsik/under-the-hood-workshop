import {Injectable} from "@nestjs/common";
import {MenuItem} from "./menu.model";
import {MenuRepository} from "./menu.repository";

// Clean service: interceptors handle cross-cutting concerns.
@Injectable()
export class MenuService {
    constructor(private readonly repository: MenuRepository) {
    }

    listItems(): MenuItem[] {
        return this.repository.findAll();
    }
}
