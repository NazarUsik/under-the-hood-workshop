from order.model import Order
from order.repository import OrderRepository


# Exercise 3: A second repository implementation returning different data.
# Swap get_order_repository() in main.py to use this instead.
class PostgresOrderRepository(OrderRepository):

    def __init__(self):
        self._orders = [
            Order(id=1, drink="Flat White", status="ready"),
            Order(id=2, drink="Mocha", status="preparing"),
            Order(id=3, drink="Cold Brew", status="pending"),
            Order(id=4, drink="Matcha Latte", status="ready"),
        ]

    def find_all(self) -> list[Order]:
        return self._orders

    def find_by_id(self, order_id: int) -> Order | None:
        return next((o for o in self._orders if o.id == order_id), None)
