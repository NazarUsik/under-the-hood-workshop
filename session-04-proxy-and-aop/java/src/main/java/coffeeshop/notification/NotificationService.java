package coffeeshop.notification;

import org.springframework.stereotype.Service;

// Clean service: no logging code. Aspects observe it.
@Service
public class NotificationService {

    public void notifyCustomer(int orderId, String message) {
        System.out.println("[Notification] Order #" + orderId + ": " + message);
    }
}
