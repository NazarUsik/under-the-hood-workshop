package coffeeshop.kitchen;

// Interface: both RealKitchenService and ChaosKitchenService implement this.
// DI picks which one to inject based on the active Spring profile.
public interface KitchenService {
    Preparation prepare(int orderId, String drink);
}
