# Session 1: The IoC Flip - Go

> Library: net/http (stdlib) | Framework: Gin

## Prerequisites

- Go 1.22+

## Project Structure

```
go/
├── library/     # net/http stdlib - you control everything
└── framework/   # Gin - the framework calls you
```

## How to Run

**Library version:**

```bash
cd library
go run .
```

Server starts on http://localhost:8080. Try `GET /orders`.

**Framework version:**

```bash
cd framework
go run .
```

Server starts on http://localhost:8080. Try `GET /orders`.

## What to Look At

- **`library/main.go`** - You call `http.HandleFunc()` to register a handler, then `http.ListenAndServe()` to start the server. You manually set headers and encode JSON. You own
  the `main()` function entirely.
- **`framework/main.go`** - Gin provides `r.GET()` for routing, `c.JSON()` for responses, and `r.Run()` for the server. Gin adds middleware, error recovery, and structured logging
  on top.

## The IoC Flip in Go

Go is interesting because the stdlib `net/http` already has a hint of IoC: `ListenAndServe` takes control of the main goroutine and calls your handler function when requests
arrive. You register the handler, the stdlib calls it.

Gin pushes this further. It owns the router, adds middleware (logging, panic recovery), manages the request context, and gives you a structured API (`c.JSON`, `c.Bind`, `c.Param`).
The pattern is the same, but Gin takes more control away from you.

Go's simplicity makes the comparison clean: both versions are short, both are readable, but the control flow is different.

## Exercises

See the [session README](../README.md#try-it-yourself) for the full exercise list. In short:

1. Run both versions and hit `GET /orders`
2. Add `GET /menu` to both - notice the difference in effort
3. Add request logging to both
4. Return an error from a handler and compare error behavior

> **Solutions:** See the [`solutions/session-1/go`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-1/go) branch.
