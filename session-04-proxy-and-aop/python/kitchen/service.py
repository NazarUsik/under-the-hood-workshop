from dataclasses import dataclass


@dataclass
class Preparation:
    order_id: int
    drink: str
    status: str


class KitchenService:
    def prepare(self, order_id: int, drink: str) -> Preparation:
        return Preparation(order_id=order_id, drink=drink, status="preparing")
