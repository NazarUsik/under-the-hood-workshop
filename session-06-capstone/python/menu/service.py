from menu.model import MenuItem
from menu.repository import MenuRepository


class MenuService:
    def __init__(self, repo: MenuRepository):
        self.repo = repo

    def list_items(self) -> list[MenuItem]:
        return self.repo.find_all()
