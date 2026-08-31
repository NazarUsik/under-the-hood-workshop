import random
import time
from abc import ABC, abstractmethod
from dataclasses import dataclass


@dataclass
class Preparation:
    order_id: int
    drink: str
    status: str


class KitchenService(ABC):
    @abstractmethod
    def prepare(self, order_id: int, drink: str) -> Preparation:
        ...


class RealKitchenService(KitchenService):
    def prepare(self, order_id: int, drink: str) -> Preparation:
        return Preparation(order_id=order_id, drink=drink, status="preparing")


class ChaosKitchenService(KitchenService):
    def prepare(self, order_id: int, drink: str) -> Preparation:
        roll = random.random()

        if roll < 0.3:
            raise RuntimeError(f"Kitchen equipment malfunction! Order #{order_id} failed.")

        if roll < 0.5:
            delay = random.uniform(2.0, 6.0)
            print(f"[Chaos] Injecting {delay:.1f}s latency for order #{order_id}")
            time.sleep(delay)

        if roll < 0.6:
            print(f"[Chaos] Returning bad data for order #{order_id}")
            return Preparation(order_id=order_id, drink=drink, status="UNKNOWN_STATUS")

        return Preparation(order_id=order_id, drink=drink, status="preparing")
