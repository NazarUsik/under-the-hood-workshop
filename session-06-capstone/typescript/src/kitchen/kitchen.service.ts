import {Injectable} from "@nestjs/common";

export interface Preparation {
    orderId: number;
    drink: string;
    status: string;
}

export abstract class KitchenService {
    abstract prepare(orderId: number, drink: string): Preparation;
}

@Injectable()
export class RealKitchenService extends KitchenService {
    prepare(orderId: number, drink: string): Preparation {
        return {orderId, drink, status: "preparing"};
    }
}

@Injectable()
export class ChaosKitchenService extends KitchenService {
    prepare(orderId: number, drink: string): Preparation {
        const roll = Math.random();

        if (roll < 0.3) {
            throw new Error(`Kitchen equipment malfunction! Order #${orderId} failed.`);
        }

        if (roll < 0.5) {
            const delay = 2000 + Math.random() * 4000;
            console.log(`[Chaos] Injecting ${delay.toFixed(0)}ms latency for order #${orderId}`);
            const start = Date.now();
            while (Date.now() - start < delay) {
            } // busy-wait (sync)
        }

        if (roll < 0.6) {
            console.log(`[Chaos] Returning bad data for order #${orderId}`);
            return {orderId, drink, status: "UNKNOWN_STATUS"};
        }

        return {orderId, drink, status: "preparing"};
    }
}
