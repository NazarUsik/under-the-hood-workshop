import {KitchenService, Preparation} from "../kitchen/kitchen.service";

export class RetryKitchenService extends KitchenService {
    constructor(
        private readonly inner: KitchenService,
        private readonly maxAttempts: number,
        private readonly delayMs: number,
    ) {
        super();
    }

    prepare(orderId: number, drink: string): Preparation {
        let lastError: Error;
        for (let attempt = 1; attempt <= this.maxAttempts; attempt++) {
            try {
                const result = this.inner.prepare(orderId, drink);
                if (attempt > 1) {
                    console.log(`[Retry] Succeeded on attempt ${attempt} for order #${orderId}`);
                }
                return result;
            } catch (e) {
                lastError = e as Error;
                console.log(
                    `[Retry] Attempt ${attempt}/${this.maxAttempts} failed for order #${orderId}: ${lastError.message}`,
                );
                if (attempt < this.maxAttempts && this.delayMs > 0) {
                    const start = Date.now();
                    while (Date.now() - start < this.delayMs) {
                    } // busy-wait
                }
            }
        }
        throw new Error(`All ${this.maxAttempts} attempts failed for order #${orderId}: ${lastError!.message}`);
    }
}
