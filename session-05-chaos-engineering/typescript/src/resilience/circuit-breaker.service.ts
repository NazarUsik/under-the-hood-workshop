import {KitchenService, Preparation} from "../kitchen/kitchen.service";

// Exercise 4: Circuit breaker wrapper.
export class CircuitBreakerKitchenService extends KitchenService {
    private consecutiveFailures = 0;
    private openedAt = 0;
    private isOpen = false;

    constructor(
        private readonly inner: KitchenService,
        private readonly failureThreshold: number = 3,
        private readonly openDurationMs: number = 10000,
    ) {
        super();
    }

    prepare(orderId: number, drink: string): Preparation {
        if (this.isOpen) {
            if (Date.now() - this.openedAt > this.openDurationMs) {
                console.log(`[CircuitBreaker] Half-open: trying one request for order #${orderId}`);
                this.isOpen = false;
                this.consecutiveFailures = 0;
            } else {
                throw new Error(`Circuit breaker is OPEN. Rejecting order #${orderId}`);
            }
        }

        try {
            const result = this.inner.prepare(orderId, drink);
            this.consecutiveFailures = 0;
            return result;
        } catch (e: any) {
            this.consecutiveFailures++;
            if (this.consecutiveFailures >= this.failureThreshold) {
                this.isOpen = true;
                this.openedAt = Date.now();
                console.log(`[CircuitBreaker] OPENED after ${this.consecutiveFailures} consecutive failures`);
            }
            throw e;
        }
    }
}
