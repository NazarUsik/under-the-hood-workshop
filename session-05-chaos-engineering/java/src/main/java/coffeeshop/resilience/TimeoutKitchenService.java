package coffeeshop.resilience;

import coffeeshop.kitchen.KitchenService;
import coffeeshop.kitchen.Preparation;

import java.util.concurrent.*;

// A proxy that wraps KitchenService with a timeout.
// If the real service doesn't respond in time, this throws a TimeoutException.
// Same Proxy pattern from Session 4, now used for resilience.
public class TimeoutKitchenService implements KitchenService {

    private final KitchenService inner;
    private final long timeoutMs;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public TimeoutKitchenService(KitchenService inner, long timeoutMs) {
        this.inner = inner;
        this.timeoutMs = timeoutMs;
    }

    @Override
    public Preparation prepare(int orderId, String drink) {
        Future<Preparation> future = executor.submit(() -> inner.prepare(orderId, drink));
        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new RuntimeException("Kitchen timed out after " + timeoutMs + "ms for order #" + orderId);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for kitchen", e);
        }
    }
}
