from dataclasses import dataclass


@dataclass
class Order:
    id: int
    drink: str
    status: str
