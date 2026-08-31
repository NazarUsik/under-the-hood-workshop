from fastapi import FastAPI, Depends, HTTPException

from order.model import Order
from order.repository import InMemoryOrderRepository, OrderRepository
from order.service import OrderService
from menu.model import MenuItem
from menu.repository import InMemoryMenuRepository, MenuRepository
from menu.service import MenuService
from proxy.decorators import logged, timed, audited
from proxy.cached import cached
from proxy.validated import validated_order_id

app = FastAPI()


# --- Apply decorators (proxies) to service methods ---
# This is Python's version of AOP. We wrap service methods with decorators
# AFTER the class is defined. The service code has no logging or timing.
#
# Decorator stacking order: outermost runs first.
# @logged(@timed(@audited(real_method)))
# -> Logging.before -> Timing.before -> Audit.before -> real -> Audit.after -> Timing.after -> Logging.after

OrderService.list_orders = logged(timed(audited(OrderService.list_orders)))
OrderService.find_order = logged(timed(audited(validated_order_id(OrderService.find_order))))
MenuService.list_items = logged(timed(cached(MenuService.list_items)))


# --- DI wiring ---
def get_order_repository() -> OrderRepository:
    return InMemoryOrderRepository()


def get_order_service(repo: OrderRepository = Depends(get_order_repository)) -> OrderService:
    return OrderService(repo)


def get_menu_repository() -> MenuRepository:
    return InMemoryMenuRepository()


def get_menu_service(repo: MenuRepository = Depends(get_menu_repository)) -> MenuService:
    return MenuService(repo)


# --- Routes ---
@app.get("/orders")
def list_orders(service: OrderService = Depends(get_order_service)) -> list[dict]:
    return [vars(o) for o in service.list_orders()]


@app.get("/orders/{order_id}")
def get_order(order_id: int, service: OrderService = Depends(get_order_service)):
    order = service.find_order(order_id)
    if order is None:
        raise HTTPException(status_code=404, detail="Order not found")
    return vars(order)


@app.get("/menu")
def list_menu(service: MenuService = Depends(get_menu_service)) -> list[dict]:
    return [vars(item) for item in service.list_items()]


if __name__ == "__main__":
    import uvicorn

    print("Server running on http://localhost:8080")
    print("Try: curl http://localhost:8080/orders")
    uvicorn.run(app, host="0.0.0.0", port=8080)
