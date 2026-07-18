# Session 1: The IoC Flip - Python

> Library: http.server (stdlib) | Framework: FastAPI

## Prerequisites

- Python 3.11+
- pip

## Project Structure

```
python/
├── library/     # stdlib http.server - you control everything
└── framework/   # FastAPI - the framework calls you
```

## How to Run

**Library version:**

```bash
cd library
python main.py
```

Server starts on http://localhost:8080. Try `GET /orders`.

**Framework version:**

```bash
cd framework
pip install -r requirements.txt
uvicorn main:app --port 8080
```

Server starts on http://localhost:8080. Try `GET /orders`.

## What to Look At

- **`library/main.py`** - You subclass `BaseHTTPRequestHandler`, parse the path yourself, manually set headers, serialize JSON, and call `serve_forever()`. About 30 lines of
  plumbing for one endpoint.
- **`framework/main.py`** - You write a function, decorate it with `@app.get("/orders")`, and return a list. FastAPI handles serialization, content types, error responses, and the
  server lifecycle. About 10 lines.

## The IoC Flip in Python

Python makes the contrast dramatic. The stdlib approach feels like writing C: manual string comparisons on paths, manual header management, manual byte encoding.

FastAPI flips it completely. You declare a function with a decorator, and the framework figures out everything else: which HTTP method, what path, how to serialize the response,
what content type to use. You write business logic, FastAPI does the plumbing.

The `@app.get("/orders")` decorator is literally the framework saying: "I'll call this function when the time is right."

## Exercises

See the [session README](../README.md#try-it-yourself) for the full exercise list. In short:

1. Run both versions and hit `GET /orders`
2. Add `GET /menu` to both - notice the difference in effort
3. Add request logging to both
4. Raise an exception from a handler and compare error behavior

> **Solutions:** See the [`solutions/session-1/python`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-1/python) branch.
