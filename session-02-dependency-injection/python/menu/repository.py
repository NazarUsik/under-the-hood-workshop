from abc import ABC, abstractmethod

from menu.model import MenuItem


class MenuRepository(ABC):

    @abstractmethod
    def find_all(self) -> list[MenuItem]:
        ...


class InMemoryMenuRepository(MenuRepository):

    def __init__(self):
        self._items = [
            MenuItem(id=1, name="Latte", price=4.50),
            MenuItem(id=2, name="Espresso", price=3.00),
            MenuItem(id=3, name="Cappuccino", price=4.00),
            MenuItem(id=4, name="Americano", price=3.50),
        ]

    def find_all(self) -> list[MenuItem]:
        return self._items
