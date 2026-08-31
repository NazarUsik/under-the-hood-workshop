class NotificationService:
    def notify_customer(self, order_id: int, message: str) -> None:
        print(f"[Notification] Order #{order_id}: {message}")
