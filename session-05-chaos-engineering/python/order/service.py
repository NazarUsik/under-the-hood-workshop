from typing import Optional

from order.model import Order
from order.repository import OrderRepository
from kitchen.service import KitchenService, Preparation


class OrderService:
    def __init__(self, repo: OrderRepository, kitchen: KitchenService):
        self.repo = repo
        self.kitchen = kitchen

    def list_orders(self) -> list[Order]:
        return self.repo.find_all()

    def find_order(self, order_id: int) -> Optional[Order]:
        return self.repo.find_by_id(order_id)

    def place_order(self, order_id: int, drink: str) -> Preparation:
        return self.kitchen.prepare(order_id, drink)
