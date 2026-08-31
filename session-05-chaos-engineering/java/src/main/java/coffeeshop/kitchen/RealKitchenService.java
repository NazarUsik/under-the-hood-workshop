package coffeeshop.kitchen;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

// Active when NOT in chaos mode (default profile).
@Service
@Profile("!chaos")
public class RealKitchenService implements KitchenService {

    @Override
    public Preparation prepare(int orderId, String drink) {
        return new Preparation(orderId, drink, "preparing");
    }
}
