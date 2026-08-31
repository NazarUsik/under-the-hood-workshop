package coffeeshop.resilience;

import coffeeshop.kitchen.KitchenService;
import coffeeshop.kitchen.Preparation;

public class RetryKitchenService implements KitchenService {

    private final KitchenService inner;
    private final int maxAttempts;
    private final long delayMs;

    public RetryKitchenService(KitchenService inner, int maxAttempts, long delayMs) {
        this.inner = inner;
        this.maxAttempts = maxAttempts;
        this.delayMs = delayMs;
    }

    @Override
    public Preparation prepare(int orderId, String drink) {
        Exception lastException = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                Preparation result = inner.prepare(orderId, drink);
                if (attempt > 1) {
                    System.out.println("[Retry] Succeeded on attempt " + attempt + " for order #" + orderId);
                }
                return result;
            } catch (Exception e) {
                lastException = e;
                System.out.println("[Retry] Attempt " + attempt + "/" + maxAttempts
                    + " failed for order #" + orderId + ": " + e.getMessage());
                if (attempt < maxAttempts && delayMs > 0) {
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted during retry", ie);
                    }
                }
            }
        }
        throw new RuntimeException("All " + maxAttempts + " attempts failed for order #" + orderId, lastException);
    }
}
