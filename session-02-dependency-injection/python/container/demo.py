"""
Demonstrates the custom DI container wiring the same classes that FastAPI's Depends() wires.
Run directly: python -m container.demo
"""

import sys
import os

# Add parent directory to path so we can import order package
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from container.container import Container
from order.repository import InMemoryOrderRepository, OrderRepository
from order.service import OrderService


def main():
    container = Container()

    # Register: interface -> implementation
    container.register(OrderRepository, InMemoryOrderRepository)

    # Resolve: the container reads OrderService.__init__,
    # sees it needs OrderRepository, resolves that first, then creates OrderService.
    service = container.resolve(OrderService)

    print("Orders from custom DI container:")
    for order in service.list_orders():
        print(f"  #{order.id} {order.drink} ({order.status})")


if __name__ == "__main__":
    main()
