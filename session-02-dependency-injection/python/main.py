from fastapi import Depends, FastAPI
from fastapi.responses import JSONResponse

from menu.model import MenuItem
from menu.repository import InMemoryMenuRepository, MenuRepository
from menu.service import MenuService
from order.model import Order
from order.repository import OrderRepository
from order.postgres_repository import PostgresOrderRepository
from order.service import OrderService

app = FastAPI()


# Dependency factory functions - this is how FastAPI does DI.
# Each function returns an instance. Depends() calls the function
# and passes the result to the route handler.

# Exercise 3: Swapped to PostgresOrderRepository (just change the factory).
def get_order_repository() -> OrderRepository:
    return PostgresOrderRepository()


def get_order_service(repo: OrderRepository = Depends(get_order_repository)) -> OrderService:
    # Nested Depends(): FastAPI resolves the chain automatically.
    # repo is injected by calling get_order_repository() first.
    return OrderService(repo)


def get_menu_repository() -> MenuRepository:
    return InMemoryMenuRepository()


def get_menu_service(repo: MenuRepository = Depends(get_menu_repository)) -> MenuService:
    return MenuService(repo)


@app.get("/orders")
def list_orders(service: OrderService = Depends(get_order_service)) -> list[Order]:
    return service.list_orders()


@app.get("/orders/{order_id}")
def get_order(order_id: int, service: OrderService = Depends(get_order_service)):
    order = service.find_order(order_id)
    if order is None:
        return JSONResponse(status_code=404, content={"detail": "Order not found"})
    return order


@app.get("/menu")
def list_menu(service: MenuService = Depends(get_menu_service)) -> list[MenuItem]:
    return service.list_items()
