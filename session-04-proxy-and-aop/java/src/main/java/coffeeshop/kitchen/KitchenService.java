package coffeeshop.kitchen;

import org.springframework.stereotype.Service;

// Clean service: aspects add logging and timing around this.
@Service
public class KitchenService {

    public Preparation prepare(int orderId, String drink) {
        return new Preparation(orderId, drink, "preparing");
    }
}
