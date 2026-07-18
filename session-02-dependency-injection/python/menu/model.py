from dataclasses import dataclass


@dataclass
class MenuItem:
    id: int
    name: str
    price: float
