# Session 6: Putting It All Together - Python

> FastAPI: all patterns combined. `apply_tracing()` X-rays services at runtime. Tests verify the invisible.

## Prerequisites

- Python 3.11+
- pip

## Project Structure

```
python/
├── main.py                          # FastAPI app + chaos/normal toggle
├── order/ menu/ kitchen/            # DI: Session 2, Chaos: Session 5
├── resilience/                      # Resilience: Session 5
│   ├── timeout.py / retry.py / fallback.py
├── tracing/                         # NEW: Session 6
│   ├── collector.py                 # TraceCollector (thread-safe step recorder)
│   └── aspect.py                   # apply_tracing(): wraps methods at runtime
├── tests/
│   └── test_trace.py               # Trace-based tests (4 tests)
└── requirements.txt
```

## How to Run

```bash
pip install -r requirements.txt
python main.py                       # normal mode
CHAOS_MODE=true python main.py       # chaos mode
pytest tests/ -v -s                  # run trace-based tests
```

## What's New in Session 6

- **`tracing/collector.py`** - `TraceCollector`: thread-safe list of steps.
- **`tracing/aspect.py`** - `apply_tracing(obj, collector)`: wraps all public methods at runtime. Zero lines added to any service class.
- **`tests/test_trace.py`** - 4 tests: normal trace, chaos resilience trace, stress test with 20 requests, e2e via TestClient.

## Exercises

1. Run `pytest tests/ -v -s`. Read the trace output for each test.
2. Remove `FallbackKitchenService` from the chaos chain. Which test fails?
3. Add a rate limiter wrapper. Write a test that verifies it fires before the service call.

> **Solutions:** See the [`solutions/session-6/python`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-6/python) branch.
