from order.model import Order
from order.repository import OrderRepository


class OrderService:
    """Business logic for orders. Depends on OrderRepository (injected, never created here)."""

    def __init__(self, repository: OrderRepository):
        self.repository = repository

    def list_orders(self) -> list[Order]:
        return self.repository.find_all()

    def find_order(self, order_id: int) -> Order | None:
        return self.repository.find_by_id(order_id)
