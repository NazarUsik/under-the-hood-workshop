import {KitchenService, Preparation} from "../kitchen/kitchen.service";

// Exercise 3: Retry wrapper. Retries up to maxAttempts times.
export class RetryKitchenService extends KitchenService {
    constructor(
        private readonly inner: KitchenService,
        private readonly maxAttempts: number = 3,
        private readonly delayMs: number = 1000,
    ) {
        super();
    }

    prepare(orderId: number, drink: string): Preparation {
        let lastError: Error | null = null;
        for (let attempt = 1; attempt <= this.maxAttempts; attempt++) {
            try {
                const result = this.inner.prepare(orderId, drink);
                if (attempt > 1) {
                    console.log(`[Retry] Succeeded on attempt ${attempt} for order #${orderId}`);
                }
                return result;
            } catch (e: any) {
                lastError = e;
                console.log(`[Retry] Attempt ${attempt}/${this.maxAttempts} failed for order #${orderId}: ${e.message}`);
                if (attempt < this.maxAttempts) {
                    const start = Date.now();
                    while (Date.now() - start < this.delayMs) {
                    }
                }
            }
        }
        throw new Error(`All ${this.maxAttempts} attempts failed for order #${orderId}`);
    }
}
