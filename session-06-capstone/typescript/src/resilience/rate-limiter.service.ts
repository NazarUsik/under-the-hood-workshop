import { KitchenService, Preparation } from "../kitchen/kitchen.service";

// Exercise: Rate limiter wrapper. Rejects if more than maxRequests in windowMs.
export class RateLimiterKitchenService extends KitchenService {
    private requestCount = 0;
    private windowStart = Date.now();

    constructor(
        private readonly inner: KitchenService,
        private readonly maxRequests: number = 10,
        private readonly windowMs: number = 60000,
    ) {
        super();
    }

    prepare(orderId: number, drink: string): Preparation {
        const now = Date.now();
        if (now - this.windowStart > this.windowMs) {
            this.windowStart = now;
            this.requestCount = 0;
        }

        this.requestCount++;
        if (this.requestCount > this.maxRequests) {
            throw new Error(`Rate limit exceeded: max ${this.maxRequests} requests per minute`);
        }

        console.log(`[RateLimiter] Request ${this.requestCount}/${this.maxRequests}`);
        return this.inner.prepare(orderId, drink);
    }
}
