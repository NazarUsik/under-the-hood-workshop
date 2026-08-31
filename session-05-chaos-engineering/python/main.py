import os
from dataclasses import asdict
from fastapi import FastAPI, Depends, HTTPException

from kitchen.service import KitchenService, RealKitchenService, ChaosKitchenService
from menu.repository import InMemoryMenuRepository, MenuRepository
from menu.service import MenuService
from order.repository import InMemoryOrderRepository, OrderRepository
from order.service import OrderService
from resilience.fallback import FallbackKitchenService
from resilience.timeout import TimeoutKitchenService

app = FastAPI()

# Toggle chaos via CHAOS_MODE env var
CHAOS_MODE = os.getenv("CHAOS_MODE", "false").lower() == "true"


# --- DI wiring ---
def get_order_repository() -> OrderRepository:
    return InMemoryOrderRepository()


def get_kitchen_service() -> KitchenService:
    if CHAOS_MODE:
        # Chaos mode: Fallback(Timeout(Chaos))
        chaos = ChaosKitchenService()
        with_timeout = TimeoutKitchenService(chaos, timeout_seconds=3.0)
        return FallbackKitchenService(with_timeout)
    return RealKitchenService()


def get_order_service(
        repo: OrderRepository = Depends(get_order_repository),
        kitchen: KitchenService = Depends(get_kitchen_service),
) -> OrderService:
    return OrderService(repo, kitchen)


def get_menu_repository() -> MenuRepository:
    return InMemoryMenuRepository()


def get_menu_service(repo: MenuRepository = Depends(get_menu_repository)) -> MenuService:
    return MenuService(repo)


# --- Routes ---
@app.get("/orders")
def list_orders(service: OrderService = Depends(get_order_service)) -> list[dict]:
    return [asdict(o) for o in service.list_orders()]


@app.get("/orders/{order_id}")
def get_order(order_id: int, service: OrderService = Depends(get_order_service)):
    order = service.find_order(order_id)
    if order is None:
        raise HTTPException(status_code=404, detail="Order not found")
    return asdict(order)


@app.post("/orders/{order_id}/prepare")
def prepare_order(order_id: int, service: OrderService = Depends(get_order_service)):
    order = service.find_order(order_id)
    if order is None:
        raise HTTPException(status_code=404, detail="Order not found")
    result = service.place_order(order.id, order.drink)
    return asdict(result)


@app.get("/menu")
def list_menu(service: MenuService = Depends(get_menu_service)) -> list[dict]:
    return [asdict(item) for item in service.list_items()]


if __name__ == "__main__":
    import uvicorn

    mode = "CHAOS" if CHAOS_MODE else "NORMAL"
    print(f"Server running on http://localhost:8080 [{mode} mode]")
    print("Try: curl -X POST http://localhost:8080/orders/1/prepare")
    uvicorn.run(app, host="0.0.0.0", port=8080)
