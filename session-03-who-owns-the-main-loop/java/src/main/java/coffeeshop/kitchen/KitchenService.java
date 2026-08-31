package coffeeshop.kitchen;

import org.springframework.stereotype.Service;

@Service
public class KitchenService {

    public Preparation prepare(int orderId, String drink) {
        // Simulate preparation
        return new Preparation(orderId, drink, "preparing");
    }
}
