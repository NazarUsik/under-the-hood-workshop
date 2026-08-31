package coffeeshop.notification;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void notifyCustomer(int orderId, String message) {
        System.out.println("[Notification] Order #" + orderId + ": " + message);
    }
}
