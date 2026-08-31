import {KitchenService, Preparation} from "../kitchen/kitchen.service";

export class TimeoutKitchenService extends KitchenService {
    constructor(
        private readonly inner: KitchenService,
        private readonly timeoutMs: number,
    ) {
        super();
    }

    prepare(orderId: number, drink: string): Preparation {
        const start = Date.now();
        try {
            const result = this.inner.prepare(orderId, drink);
            const elapsed = Date.now() - start;
            if (elapsed > this.timeoutMs) {
                throw new Error(`Kitchen timed out after ${elapsed}ms for order #${orderId}`);
            }
            return result;
        } catch (e) {
            const elapsed = Date.now() - start;
            if (elapsed > this.timeoutMs) {
                throw new Error(`Kitchen timed out after ${elapsed}ms for order #${orderId}`);
            }
            throw e;
        }
    }
}
