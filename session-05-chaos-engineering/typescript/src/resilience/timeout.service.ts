import {KitchenService, Preparation} from "../kitchen/kitchen.service";

// Wraps KitchenService with a timeout.
// Uses a Promise race between the inner call and a timer.
export class TimeoutKitchenService extends KitchenService {
    constructor(
        private readonly inner: KitchenService,
        private readonly timeoutMs: number,
    ) {
        super();
    }

    prepare(orderId: number, drink: string): Preparation {
        const start = Date.now();
        const result = this.inner.prepare(orderId, drink);
        const elapsed = Date.now() - start;

        if (elapsed > this.timeoutMs) {
            throw new Error(`Kitchen timed out after ${elapsed}ms for order #${orderId}`);
        }

        return result;
    }
}
