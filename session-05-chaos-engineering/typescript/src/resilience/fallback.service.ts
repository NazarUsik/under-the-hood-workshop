import {KitchenService, Preparation} from "../kitchen/kitchen.service";

// Catches exceptions and returns a fallback "queued" status.
export class FallbackKitchenService extends KitchenService {
    constructor(private readonly inner: KitchenService) {
        super();
    }

    prepare(orderId: number, drink: string): Preparation {
        try {
            return this.inner.prepare(orderId, drink);
        } catch (e: any) {
            console.log(`[Fallback] Kitchen failed: ${e.message} -- returning queued for order #${orderId}`);
            return {orderId, drink, status: "queued"};
        }
    }
}
