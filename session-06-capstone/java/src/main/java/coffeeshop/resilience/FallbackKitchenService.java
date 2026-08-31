package coffeeshop.resilience;

import coffeeshop.kitchen.KitchenService;
import coffeeshop.kitchen.Preparation;

public class FallbackKitchenService implements KitchenService {

    private final KitchenService inner;

    public FallbackKitchenService(KitchenService inner) {
        this.inner = inner;
    }

    @Override
    public Preparation prepare(int orderId, String drink) {
        try {
            return inner.prepare(orderId, drink);
        } catch (Exception e) {
            System.out.println("[Fallback] Kitchen failed: " + e.getMessage()
                + " -- returning queued status for order #" + orderId);
            return new Preparation(orderId, drink, "queued");
        }
    }
}
