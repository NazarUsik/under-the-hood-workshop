from menu.model import MenuItem
from menu.repository import MenuRepository


class MenuService:

    def __init__(self, repository: MenuRepository):
        self.repository = repository

    def list_items(self) -> list[MenuItem]:
        return self.repository.find_all()
