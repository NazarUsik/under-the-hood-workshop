import time
from contextlib import asynccontextmanager

from fastapi import FastAPI, Request, HTTPException, Depends
from fastapi.responses import JSONResponse

from order.model import Order
from order.repository import InMemoryOrderRepository, OrderRepository
from order.service import OrderService
from menu.model import MenuItem
from menu.repository import InMemoryMenuRepository, MenuRepository
from menu.service import MenuService


# --- Lifecycle hooks ---
# FastAPI uses lifespan context manager for startup/shutdown events.
# The framework calls this before accepting requests and on shutdown.
@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup: runs before the server accepts any connections
    print("[Lifecycle] startup: initializing services, warming caches")
    yield
    # Shutdown: runs when the server is stopping (Ctrl+C, SIGTERM)
    print("[Lifecycle] shutdown: closing connections, flushing logs")


app = FastAPI(lifespan=lifespan)


# --- ASGI Middleware ---
# This runs for every request. call_next passes control to the next middleware or handler.
# The "before" code runs before your route handler, the "after" code runs after.
@app.middleware("http")
async def logging_middleware(request: Request, call_next):
    start = time.time()
    print(f"[Middleware] >> {request.method} {request.url.path}")

    response = await call_next(request)

    duration_ms = (time.time() - start) * 1000
    print(f"[Middleware] << {response.status_code} ({duration_ms:.0f}ms)")
    return response


# --- Error handling ---
# FastAPI catches exceptions and calls the matching handler.
# You don't need try/except in your route handlers.
@app.exception_handler(ValueError)
async def value_error_handler(request: Request, exc: ValueError):
    print(f"[ErrorHandler] Bad request: {exc}")
    return JSONResponse(status_code=400, content={"error": str(exc)})


# --- DI wiring via Depends() ---
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
