# Session 4: Proxy Pattern and AOP - Python

> FastAPI: Decorators as proxies, `functools.wraps`, decorator stacking, runtime method patching

## Prerequisites

- Python 3.11+
- pip

## Project Structure

```
python/
├── main.py                         # FastAPI app + decorator application
├── order/
│   ├── model.py                    # Order dataclass
│   ├── repository.py               # ABC + InMemory implementation
│   └── service.py                  # OrderService (NO logging code!)
├── menu/
│   ├── model.py                    # MenuItem dataclass
│   ├── repository.py               # ABC + InMemory implementation
│   └── service.py                  # MenuService (clean, no cross-cutting code)
├── kitchen/
│   └── service.py                  # KitchenService + Preparation
├── notification/
│   └── service.py                  # NotificationService
├── proxy/
│   └── decorators.py               # @logged, @timed, @audited decorators
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

Watch the console: `[Logging]`, `[Timing]`, and `[Audit]` messages appear even though the service code has zero logging.

## What to Look At

- **`order/service.py`** and **`menu/service.py`** - pure business logic. No logging, no timing. Compare them to the console output.
- **`proxy/decorators.py`** - three decorator functions that ARE proxies. Each takes a function and returns a wrapper. `@wraps(func)` preserves the original function name and
  docstring.
- **`main.py` lines 21-23** - this is where decorators are applied to service methods *after* class definition. This is Python's runtime equivalent of Spring's CGLIB proxy
  creation.

## Python Decorators as Proxies

A decorator is syntactic sugar for wrapping a function:

```python
@timed
def list_orders():
    ...


# is exactly the same as:
def list_orders():
    ...


list_orders = timed(list_orders)
```

When you call `list_orders()`, you're calling the wrapper function returned by `timed()`. The wrapper calls the original `list_orders()` inside. This IS the Proxy pattern.

### Decorator Stacking = Proxy Chain

```python
@logged
@timed
@audited
def list_orders():
    ...
```

This creates: `logged(timed(audited(list_orders)))`. Execution order:

```
logged.before -> timed.before -> audited.before -> list_orders -> audited.after -> timed.after -> logged.after
```

The outermost decorator (`@logged`) runs first and last, just like `@Order(1)` in Spring AOP.

### Two Ways to Apply Decorators

**At definition time** (standard Python):

```python
class OrderService:
    @logged
    @timed
    def list_orders(self):
        ...
```

**At runtime** (what this project does):

```python
OrderService.list_orders = logged(timed(OrderService.list_orders))
```

The runtime approach keeps the service class completely clean, just like Spring AOP. The service doesn't know it's being proxied.

### Why `@wraps(func)` Matters

Without `@wraps`, the wrapper function replaces the original function's name and docstring:

```python
def timed(func):
    def wrapper(*args, **kwargs):  # wrapper.__name__ is "wrapper", not "list_orders"
        ...

    return wrapper


# With @wraps:
def timed(func):
    @wraps(func)
    def wrapper(*args, **kwargs):  # wrapper.__name__ is "list_orders"
        ...

    return wrapper
```

This matters for debugging, logging, and FastAPI's automatic API docs (which use function names for endpoint names).

### Class-Based Proxy (Alternative Approach)

Instead of function decorators, you can create a proxy class that wraps a service:

```python
class LoggingOrderService:
    def __init__(self, inner: OrderService):
        self._inner = inner

    def list_orders(self):
        print(f"[Logging] >> list_orders")
        result = self._inner.list_orders()
        print(f"[Logging] << list_orders returned {result}")
        return result
```

This is the same as Go's wrapper struct approach. Same interface, wraps the real object, adds behavior.

## Exercises

See the [session README](../README.md#try-it-yourself) for the full exercise list. In this Python project:

1. Run the app, hit `GET /orders`, and read the console output. Trace the decorator chain: Logging -> Timing -> Audit -> real method -> Audit -> Timing -> Logging.
2. Write a `@cached` decorator for `MenuService.list_items()`. First call hits the repository, subsequent calls return the cached result.
3. Write a `@validated` decorator for `OrderService.find_order()` that checks `order_id > 0`.
4. Stack three decorators on `OrderService` and verify the execution order in the logs.
5. Create a class-based proxy `LoggingOrderService` that wraps `OrderService` (same as Go's approach). Swap it into the DI wiring.

> **Solutions:** See the [`solutions/session-4/python`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-4/python) branch.
