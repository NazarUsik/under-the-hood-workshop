from fastapi import Depends, FastAPI
from fastapi.responses import JSONResponse

from order.model import Order
from order.repository import InMemoryOrderRepository, OrderRepository
from order.service import OrderService

app = FastAPI()


# Dependency factory functions - this is how FastAPI does DI.
# Each function returns an instance. Depends() calls the function
# and passes the result to the route handler.

def get_order_repository() -> OrderRepository:
    return InMemoryOrderRepository()


def get_order_service(repo: OrderRepository = Depends(get_order_repository)) -> OrderService:
    # Nested Depends(): FastAPI resolves the chain automatically.
    # repo is injected by calling get_order_repository() first.
    return OrderService(repo)


@app.get("/orders")
def list_orders(service: OrderService = Depends(get_order_service)) -> list[Order]:
    return service.list_orders()


@app.get("/orders/{order_id}")
def get_order(order_id: int, service: OrderService = Depends(get_order_service)):
    order = service.find_order(order_id)
    if order is None:
        return JSONResponse(status_code=404, content={"detail": "Order not found"})
    return order
