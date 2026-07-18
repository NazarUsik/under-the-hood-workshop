"""Exercise 4: Unit test with a stub repository. No FastAPI, no HTTP."""

from order.model import Order
from order.repository import OrderRepository
from order.service import OrderService


class StubOrderRepository(OrderRepository):

    def __init__(self):
        self._orders = [
            Order(id=1, drink="Test Latte", status="ready"),
            Order(id=2, drink="Test Espresso", status="preparing"),
        ]

    def find_all(self) -> list[Order]:
        return self._orders

    def find_by_id(self, order_id: int) -> Order | None:
        return next((o for o in self._orders if o.id == order_id), None)


# DI by hand: pass the stub to the constructor.
service = OrderService(StubOrderRepository())


def test_list_orders():
    orders = service.list_orders()
    assert len(orders) == 2
    assert orders[0].drink == "Test Latte"


def test_find_order():
    order = service.find_order(2)
    assert order is not None
    assert order.drink == "Test Espresso"


def test_find_order_missing():
    order = service.find_order(99)
    assert order is None
