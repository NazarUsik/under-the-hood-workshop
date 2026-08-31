package coffeeshop.resilience;

import coffeeshop.kitchen.KitchenService;
import coffeeshop.kitchen.Preparation;

// Exercise 4: Simple circuit breaker proxy.
// Opens after failureThreshold consecutive failures, stays open for openDurationMs.
public class CircuitBreakerKitchenService implements KitchenService {

    private final KitchenService inner;
    private final int failureThreshold;
    private final long openDurationMs;

    private int consecutiveFailures = 0;
    private long openedAt = 0;
    private boolean open = false;

    public CircuitBreakerKitchenService(KitchenService inner, int failureThreshold, long openDurationMs) {
        this.inner = inner;
        this.failureThreshold = failureThreshold;
        this.openDurationMs = openDurationMs;
    }

    @Override
    public Preparation prepare(int orderId, String drink) {
        if (open) {
            if (System.currentTimeMillis() - openedAt > openDurationMs) {
                System.out.println("[CircuitBreaker] Half-open: trying one request for order #" + orderId);
                open = false;
                consecutiveFailures = 0;
            } else {
                throw new RuntimeException("Circuit breaker is OPEN. Rejecting order #" + orderId);
            }
        }

        try {
            Preparation result = inner.prepare(orderId, drink);
            consecutiveFailures = 0;
            return result;
        } catch (Exception e) {
            consecutiveFailures++;
            if (consecutiveFailures >= failureThreshold) {
                open = true;
                openedAt = System.currentTimeMillis();
                System.out.println("[CircuitBreaker] OPENED after " + consecutiveFailures + " consecutive failures");
            }
            throw e;
        }
    }
}
