package coffeeshop.kitchen;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Random;

// Active when chaos profile is enabled: spring.profiles.active=chaos
// Randomly injects failures: exceptions, latency, or empty results.
@Service
@Profile("chaos")
public class ChaosKitchenService implements KitchenService {

    private final Random random = new Random();

    @Override
    public Preparation prepare(int orderId, String drink) {
        double roll = random.nextDouble();

        if (roll < 0.3) {
            // 30%: crash
            throw new RuntimeException("Kitchen equipment malfunction! Order #" + orderId + " failed.");
        }

        if (roll < 0.5) {
            // 20%: latency (simulate slow response)
            try {
                long delay = 2000 + random.nextInt(4000); // 2-6 seconds
                System.out.println("[Chaos] Injecting " + delay + "ms latency for order #" + orderId);
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (roll < 0.6) {
            // 10%: bad data
            System.out.println("[Chaos] Returning bad data for order #" + orderId);
            return new Preparation(orderId, drink, "UNKNOWN_STATUS");
        }

        // 40%: success
        return new Preparation(orderId, drink, "preparing");
    }
}
