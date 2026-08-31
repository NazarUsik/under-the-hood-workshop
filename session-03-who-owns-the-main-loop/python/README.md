# Session 3: Who Owns the Main Loop - Python

> FastAPI: ASGI middleware, `Depends()` chains, lifespan events, exception handlers

## Prerequisites

- Python 3.11+
- pip

## Project Structure

```
python/
├── main.py                         # FastAPI app + middleware + lifecycle + error handling
├── order/
│   ├── model.py                    # Order dataclass
│   ├── repository.py               # ABC + InMemory implementation
│   └── service.py                  # OrderService
├── menu/
│   ├── model.py                    # MenuItem dataclass
│   ├── repository.py               # ABC + InMemory implementation
│   └── service.py                  # MenuService
├── kitchen/
│   └── service.py                  # KitchenService + Preparation
├── notification/
│   └── service.py                  # NotificationService
└── requirements.txt
```

## How to Run

```bash
pip install -r requirements.txt
python main.py
```

Server starts on http://localhost:8080.

- `GET /orders` - list all orders
- `GET /orders/1` - get order by ID
- `GET /menu` - list menu items

Watch the console: you'll see middleware logging before/after every request.

## What to Look At

- **`main.py` middleware** - `@app.middleware("http")` wraps every request. `call_next(request)` is FastAPI's equivalent of `chain.doFilter()`. Code before `call_next` runs before
  your handler, code after runs after. This is the onion model.
- **`main.py` lifespan** - `@asynccontextmanager` with `yield`. Everything before `yield` is startup, everything after is shutdown. FastAPI calls this before the server starts
  accepting connections.
- **`main.py` error handler** - `@app.exception_handler(ValueError)` catches exceptions globally. FastAPI also auto-converts `HTTPException` to the right status code.
- **`main.py` Depends()** - The DI chain is visible in the route signature. FastAPI resolves the whole dependency tree per request.

## FastAPI Request Pipeline Deep Dive

### The Layers (Outermost to Innermost)

1. **Uvicorn** - accepts TCP, runs the ASGI event loop
2. **ASGI Middleware** - `@app.middleware("http")`, Starlette middleware classes
3. **Route matching** - FastAPI's router finds the handler
4. **Dependency resolution** - `Depends()` chain resolved per request
5. **Your route handler** - business logic
6. **Response serialization** - Pydantic model / dict to JSON
7. **Exception handling** - `@app.exception_handler()` catches errors

### ASGI: What It Actually Is

ASGI (Asynchronous Server Gateway Interface) is Python's protocol for connecting web servers (Uvicorn) to web applications (FastAPI). When a request arrives:

1. Uvicorn parses HTTP into a `scope` dict (method, path, headers)
2. Uvicorn calls `app(scope, receive, send)` - your FastAPI application
3. FastAPI's middleware stack processes the request
4. Your handler runs
5. FastAPI calls `send()` to write the response back through Uvicorn

You never see `scope`, `receive`, or `send` directly. FastAPI wraps them in `Request` and `Response` objects. But they're there, under the hood.

### Middleware Order Gotcha

FastAPI/Starlette runs middleware in **reverse registration order**. The last registered middleware is the outermost layer:

```python
@app.middleware("http")
async def middleware_a(request, call_next):  # registered first = innermost
    ...

@app.middleware("http")
async def middleware_b(request, call_next):  # registered second = outermost
    ...
```

Request flow: B.before -> A.before -> handler -> A.after -> B.after

This is the opposite of most frameworks. Be careful.

### Depends() as Middleware

FastAPI's `Depends()` is not just DI. It's also middleware. You can use `yield` dependencies for setup/teardown:

```python
async def get_db():
    db = connect()   # setup: before handler
    yield db
    db.close()       # teardown: after handler
```

This is the same onion model: code before `yield` runs before the handler, code after `yield` runs after.

## Exercises

See the [session README](../README.md#try-it-yourself) for the full exercise list. In this Python project:

1. Run the app, hit `GET /orders`, and read the console output. Trace the middleware -> handler -> middleware flow.
2. Add a request ID middleware that generates a UUID and includes it in the response headers (`X-Request-Id`).
3. Add an auth middleware that checks for an `Authorization` header. Return 401 JSON if missing. Verify the handler does NOT run.
4. Hit `GET /orders/999`. See the 404 response. Now make a route raise `ValueError` and see the custom error handler catch it.
5. Add a `yield` dependency that logs "DB connection opened" before and "DB connection closed" after the handler.

> **Solutions:** See the [`solutions/session-3/python`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-3/python) branch.
