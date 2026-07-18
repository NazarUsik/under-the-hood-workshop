import logging
from datetime import datetime

from fastapi import FastAPI, Request

app = FastAPI()

logger = logging.getLogger("uvicorn")


# Exercise 3: Request logging via middleware.
# FastAPI calls this for every request automatically. You don't touch your route handlers.
# This is a preview of middleware (Session 3).
@app.middleware("http")
async def log_requests(request: Request, call_next):
    logger.info(f"[{datetime.now()}] {request.method} {request.url.path}")
    return await call_next(request)


@app.get("/orders")
def list_orders():
    # FastAPI calls this function when GET /orders arrives.
    # You never call it yourself. That's IoC.
    return [
        {"id": 1, "drink": "Latte"},
        {"id": 2, "drink": "Espresso"},
        {"id": 3, "drink": "Cappuccino"},
    ]


# Exercise 2: GET /menu - one decorator, one return. Compare to the library version.
@app.get("/menu")
def list_menu():
    return [
        {"id": 1, "name": "Latte", "price": 4.50},
        {"id": 2, "name": "Espresso", "price": 3.00},
        {"id": 3, "name": "Cappuccino", "price": 4.00},
        {"id": 4, "name": "Americano", "price": 3.50},
    ]


# Exercise 4: Raise an exception. FastAPI returns a clean JSON error response
# with {"detail": "Internal Server Error"} and a 500 status. No crash.
@app.get("/error")
def simulate_error():
    raise RuntimeError("Something went wrong!")
