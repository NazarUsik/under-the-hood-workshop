# Session 5: Chaos Engineering - Python

> FastAPI: DI-based service swapping, chaos injection via env var, resilience wrappers (timeout + fallback)

## Prerequisites

- Python 3.11+
- pip

## Project Structure

```
python/
├── main.py                          # FastAPI app + chaos/normal toggle
├── order/
│   ├── model.py                     # Order dataclass
│   ├── repository.py                # ABC + InMemory implementation
│   └── service.py                   # Calls KitchenService.prepare()
├── menu/
│   ├── model.py                     # MenuItem dataclass
│   ├── repository.py                # ABC + InMemory implementation
│   └── service.py                   # MenuService
├── kitchen/
│   └── service.py                   # ABC + Real + Chaos implementations
├── resilience/
│   ├── timeout.py                   # TimeoutKitchenService wrapper
│   └── fallback.py                  # FallbackKitchenService wrapper
└── requirements.txt
```

## How to Run

**Normal mode** (everything works):
```bash
pip install -r requirements.txt
python main.py
```

**Chaos mode** (random failures):
```bash
CHAOS_MODE=true python main.py
```

Server starts on http://localhost:8080.

- `GET /orders` - list all orders
- `GET /orders/1` - get order by ID
- `POST /orders/1/prepare` - prepare an order (chaos happens here)
- `GET /menu` - list menu items

## How the DI Swap Works

FastAPI's `Depends()` lets you swap implementations at the function level:

```python
def get_kitchen_service() -> KitchenService:
    if CHAOS_MODE:
        chaos = ChaosKitchenService()
        with_timeout = TimeoutKitchenService(chaos, timeout_seconds=3.0)
        return FallbackKitchenService(with_timeout)
    return RealKitchenService()
```

`OrderService` receives a `KitchenService` via constructor injection. It doesn't know if it's real, chaos, or wrapped in resilience layers.

## What to Look At

- **`kitchen/service.py`** - `KitchenService` ABC, `RealKitchenService` (always works), `ChaosKitchenService` (30% crash, 20% latency, 10% bad data, 40% success).
- **`resilience/timeout.py`** - wraps kitchen with `concurrent.futures` timeout. If inner takes longer than 3s, raises `RuntimeError`.
- **`resilience/fallback.py`** - catches exceptions, returns `Preparation(status="queued")`.
- **`main.py` lines 27-33** - the DI swap point. `CHAOS_MODE` env var controls which implementation chain gets wired.

## Exercises

See the [session README](../README.md#try-it-yourself) for the full exercise list. In this Python project:

1. Run in normal mode. `POST /orders/1/prepare` always returns `"preparing"`.
2. Run in chaos mode. Hit `POST /orders/1/prepare` 20 times. Count successes, failures, and "queued" fallbacks.
3. Add a `RetryKitchenService` wrapper that retries up to 3 times on failure before giving up.
4. Add a `CircuitBreakerKitchenService` that opens after 3 consecutive failures and stays open for 10 seconds.
5. Stack them: `Fallback(Retry(CircuitBreaker(Timeout(chaos))))`. Hit the endpoint 50 times.

> **Solutions:** See the [`solutions/session-5/python`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-5/python) branch.
