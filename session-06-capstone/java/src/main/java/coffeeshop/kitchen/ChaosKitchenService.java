package coffeeshop.kitchen;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Profile("chaos")
public class ChaosKitchenService implements KitchenService {

    private final Random random = new Random();

    @Override
    public Preparation prepare(int orderId, String drink) {
        double roll = random.nextDouble();

        if (roll < 0.3) {
            throw new RuntimeException("Kitchen equipment malfunction! Order #" + orderId + " failed.");
        }

        if (roll < 0.5) {
            try {
                long delay = 2000 + random.nextInt(4000);
                System.out.println("[Chaos] Injecting " + delay + "ms latency for order #" + orderId);
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (roll < 0.6) {
            System.out.println("[Chaos] Returning bad data for order #" + orderId);
            return new Preparation(orderId, drink, "UNKNOWN_STATUS");
        }

        return new Preparation(orderId, drink, "preparing");
    }
}
