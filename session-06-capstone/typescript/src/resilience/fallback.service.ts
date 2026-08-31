import {KitchenService, Preparation} from "../kitchen/kitchen.service";

export class FallbackKitchenService extends KitchenService {
    constructor(private readonly inner: KitchenService) {
        super();
    }

    prepare(orderId: number, drink: string): Preparation {
        try {
            return this.inner.prepare(orderId, drink);
        } catch (e) {
            console.log(
                `[Fallback] Kitchen failed: ${(e as Error).message} -- returning queued for order #${orderId}`,
            );
            return {orderId, drink, status: "queued"};
        }
    }
}
