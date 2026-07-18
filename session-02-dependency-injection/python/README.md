# Session 2: Dependency Injection - Python

> FastAPI: `Depends()`, factory functions, and a DIY container

## Prerequisites

- Python 3.12+
- pip

## Project Structure

```
python/
├── main.py                    # FastAPI app with Depends() wiring
├── order/
│   ├── model.py               # Order dataclass
│   ├── repository.py          # Abstract base + InMemory implementation
│   └── service.py             # OrderService (depends on OrderRepository)
├── container/
│   ├── container.py           # DIY DI container (~30 lines)
│   └── demo.py                # Run this to see the custom container in action
└── requirements.txt
```

## How to Run

**FastAPI app:**

```bash
pip install -r requirements.txt
uvicorn main:app --port 8080
```

Server starts on http://localhost:8080.

- `GET /orders` - list all orders
- `GET /orders/1` - get order by ID

**Custom DI container demo:**

```bash
python -m container.demo
```

## What to Look At

- **`order/repository.py`** - An ABC (abstract base class) defining the interface + an `InMemoryOrderRepository` implementation. This is what gets swapped in tests or with a real database.
- **`order/service.py`** - Takes `OrderRepository` in its constructor. Pure Python, no framework dependency. Testable in isolation.
- **`main.py`** - Factory functions (`get_order_repository`, `get_order_service`) + `Depends()`. FastAPI calls the factory, resolves the chain, and passes the result to your route handler.
- **`container/container.py`** - A minimal DI container using `inspect.signature()` to read constructor type hints and resolve dependencies automatically.

## FastAPI Deep Dive

### How Depends() Chains Work

`Depends()` takes a callable (usually a function) and calls it to get the dependency. When your factory has its own `Depends()` parameters, FastAPI resolves them first:

```python
def get_repo() -> OrderRepository:
    return InMemoryOrderRepository()

def get_service(repo: OrderRepository = Depends(get_repo)) -> OrderService:
    return OrderService(repo)

@app.get("/orders")
def list_orders(service: OrderService = Depends(get_service)):
    return service.list_orders()
```

FastAPI walks this chain bottom-up: `get_repo()` -> `get_service(repo)` -> `list_orders(service)`. Same idea as topological sort in a DI container.

### yield Dependencies (Resource Cleanup)

`Depends()` supports generator functions with `yield`. Code before `yield` runs at request start, code after runs at request end. This is how you manage resources like database sessions:

```python
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
```

FastAPI ensures `db.close()` runs even if the request handler throws an exception. This is equivalent to Spring's `@PreDestroy` or Go's `defer`.

### Per-Request vs Shared Instances

By default, FastAPI calls the factory function on every request. That makes dependencies request-scoped. If you want a singleton, define the instance outside the function:

```python
_repo = InMemoryOrderRepository()  # created once at module load

def get_order_repository() -> OrderRepository:
    return _repo  # same instance every request
```

This is a manual version of what Spring does with `@Scope("singleton")` vs `@Scope("request")`.

### FastAPI DI vs dependency-injector

FastAPI's `Depends()` is lightweight and function-based. For larger projects, the [dependency-injector](https://python-dependency-injector.ets-labs.org/) library provides a more traditional container with singletons, factories, configuration injection, and wiring. It's closer to Spring's model but uses Python's type system.

## Exercises

See the [session README](../README.md#try-it-yourself) for the full exercise list. In this Python project:

1. Run the app and trace the DI chain: `GET /orders` -> `get_order_service` -> `get_order_repository`
2. Add a `MenuService` + `MenuRepository` and expose `GET /menu` using `Depends()`
3. Create a second repository factory that returns different data, swap it in `get_order_service`
4. Write a unit test for `OrderService` by passing a stub repository to the constructor (no FastAPI needed)
5. Run `python -m container.demo` and study `container.py` - extend it with scope support

> **Solutions:** See the [`solutions/session-2/python`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-2/python) branch.
