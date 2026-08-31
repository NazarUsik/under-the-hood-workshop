from abc import ABC, abstractmethod
from order.model import Order
from typing import Optional


class OrderRepository(ABC):
    @abstractmethod
    def find_all(self) -> list[Order]:
        ...

    @abstractmethod
    def find_by_id(self, order_id: int) -> Optional[Order]:
        ...


class InMemoryOrderRepository(OrderRepository):
    def __init__(self):
        self._orders = [
            Order(id=1, drink="Latte", status="ready"),
            Order(id=2, drink="Espresso", status="preparing"),
            Order(id=3, drink="Cappuccino", status="pending"),
        ]

    def find_all(self) -> list[Order]:
        return self._orders

    def find_by_id(self, order_id: int) -> Optional[Order]:
        return next((o for o in self._orders if o.id == order_id), None)
