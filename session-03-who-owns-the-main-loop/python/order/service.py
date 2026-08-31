from typing import Optional

from order.model import Order
from order.repository import OrderRepository


class OrderService:
    def __init__(self, repo: OrderRepository):
        self.repo = repo

    def list_orders(self) -> list[Order]:
        return self.repo.find_all()

    def find_order(self, order_id: int) -> Optional[Order]:
        return self.repo.find_by_id(order_id)
