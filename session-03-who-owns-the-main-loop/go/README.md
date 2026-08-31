# Session 3: Who Owns the Main Loop - Go

> Gin: Middleware chain, `c.Next()` / `c.Abort()`, recovery, graceful shutdown

## Prerequisites

- Go 1.22+

## Project Structure

```
go/
├── main.go                     # Entry point: manual DI, middleware pipeline, graceful shutdown
├── order/
│   ├── model.go                # Order struct
│   ├── repository.go           # Interface + InMemory implementation
│   ├── service.go              # OrderService with constructor injection
│   └── handler.go              # Gin handlers for /orders
├── menu/
│   ├── model.go                # MenuItem struct
│   ├── repository.go           # Interface + InMemory implementation
│   ├── service.go              # MenuService
│   └── handler.go              # Gin handler for /menu
├── middleware/
│   ├── logging.go              # Request logging middleware
│   └── recovery.go             # Panic recovery middleware
└── go.mod
```

## How to Run

```bash
go run .
```

Server starts on http://localhost:8080.

- `GET /orders` - list all orders
- `GET /orders/1` - get order by ID
- `GET /menu` - list menu items

Watch the console: middleware logs before and after every request.

## What to Look At

- **`middleware/logging.go`** - `c.Next()` passes control to the next handler. Code before `c.Next()` runs before your handler, code after runs after. This is Gin's middleware
  model: a chain of `HandlerFunc` functions.
- **`middleware/recovery.go`** - `defer recover()` catches panics. This is Go's error handling for middleware. Gin includes `gin.Recovery()` by default; this shows how it works.
- **`main.go`** - Everything is explicit. DI wiring, middleware order, route registration, graceful shutdown. In Go, you own the main loop. The framework is just a router.

## Go Middleware Deep Dive

### How Gin Middleware Works

Gin middleware is a chain of `gin.HandlerFunc`. When you call `r.Use(A, B, C)`:

```
Request -> A.before -> B.before -> C.before -> Handler -> C.after -> B.after -> A.after
```

`c.Next()` advances to the next handler. `c.Abort()` stops the chain immediately.

```go
func AuthMiddleware() gin.HandlerFunc {
    return func(c *gin.Context) {
        if c.GetHeader("Authorization") == "" {
            c.AbortWithStatusJSON(401, gin.H{"error": "unauthorized"})
            return // handler never runs
        }
        c.Next() // handler runs
    }
}
```

### Middleware Order Matters

```go
r.Use(Recovery())   // outermost: catches panics from everything
r.Use(Logging())    // logs request/response timing
r.Use(Auth())       // checks authorization
```

Recovery must be first (outermost) so it catches panics from logging and auth too.

### Graceful Shutdown in Go

Unlike Spring/NestJS/FastAPI, Go doesn't have built-in graceful shutdown. You do it yourself:

1. Start the server in a goroutine
2. Listen for OS signals (SIGINT, SIGTERM)
3. Call `srv.Shutdown(ctx)` with a timeout
4. Clean up resources

This is the most explicit version of lifecycle hooks. No annotations, no decorators, no framework magic. Just your code in `main()`.

### Error Handling: No Exceptions

Go doesn't have exceptions. Gin doesn't have `@ControllerAdvice` or exception filters. Instead:

- **Handler errors**: Return error JSON explicitly with `c.JSON(status, gin.H{"error": msg})`
- **Panics**: Caught by `Recovery` middleware (same as exception filters in other frameworks)
- **Middleware errors**: Call `c.Abort()` to short-circuit

This is more verbose but completely transparent. You always know exactly what happens on error.

## Exercises

See the [session README](../README.md#try-it-yourself) for the full exercise list. In this Go project:

1. Run the app, hit `GET /orders`, and read the console output. Trace middleware -> handler -> middleware.
2. Add a request ID middleware that generates a UUID and sets `X-Request-Id` response header.
3. Add an auth middleware that checks for `Authorization` header and aborts with 401 if missing.
4. Hit `GET /orders/999`. See the 404 JSON response.
5. Add a startup log message before `ListenAndServe` and a shutdown message after `srv.Shutdown()`.

> **Solutions:** See the [`solutions/session-3/go`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-3/go) branch.
