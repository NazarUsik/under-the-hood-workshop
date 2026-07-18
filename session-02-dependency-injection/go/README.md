# Session 2: Dependency Injection - Go

> Manual DI with interfaces and constructors (the idiomatic way)

## Prerequisites

- Go 1.22+

## Project Structure

```
go/
├── main.go                      # Gin HTTP server + manual DI wiring
├── order/
│   ├── model.go                 # Order struct
│   ├── repository.go            # Interface + InMemory implementation
│   ├── service.go               # OrderService (depends on OrderRepository)
│   └── handler.go               # HTTP handlers (depends on OrderService)
├── container/
│   ├── container.go             # Reflection-based DI container (non-idiomatic, for learning)
│   └── demo/main.go             # Demonstrates manual DI (the Go way)
├── go.mod
└── go.sum
```

## How to Run

**Gin server:**

```bash
go run .
```

Server starts on http://localhost:8080.

- `GET /orders` - list all orders
- `GET /orders/1` - get order by ID

**Manual DI demo (no HTTP):**

```bash
go run ./container/demo
```

## What to Look At

- **`order/repository.go`** - An interface. Go interfaces are implicit: `InMemoryOrderRepository` satisfies `OrderRepository` because it has the right methods. No `implements`, no
  annotations.
- **`order/service.go`** - `NewOrderService(repo OrderRepository)` takes the interface. Any implementation works. This is constructor injection without a framework.
- **`main.go`** - The DI wiring happens here, in plain Go. Three lines: create repo, create service, create handler. Pass them down. Done.
- **`container/container.go`** - A reflection-based container (non-idiomatic, for comparison with Spring/NestJS). Shows the same algorithm works in Go.

## Go Deep Dive

### Why Manual DI Is the Convention

Go doesn't have annotations, classpath scanning, or decorator metadata. Its type system is intentionally simple. The community leans into this: DI is just passing interfaces to
constructors. No framework needed.

This means your `main()` function (or a `wire()` helper) is the composition root. It creates all dependencies and wires them. This is explicit, readable, and easy to follow.

### Implicit Interfaces

In Java or TypeScript, you declare `implements OrderRepository`. In Go, you just implement the methods. The compiler checks compatibility at the call site, not the declaration.
This makes it trivial to define interfaces close to the consumer (not the provider), which is a Go best practice.

### Google Wire vs Uber fx

For large projects with hundreds of constructors, manual wiring gets tedious. Two popular options:

**[Google Wire](https://github.com/google/wire)** - Code generation. You define "provider sets" and Wire generates the wiring code at compile time. No runtime reflection, no
performance overhead. The generated code is plain Go.

**[Uber fx](https://github.com/uber-go/fx)** - Runtime DI container using reflection. Closer to Spring's model. You register constructors and fx resolves the dependency graph at
startup. More magic, but less boilerplate.

Most Go teams use manual DI for small-to-medium projects and Wire for large ones.

### Functional Options Pattern

When a constructor has many optional parameters, Go uses the "functional options" pattern instead of builder classes or overloaded constructors:

```go
type OrderService struct {
    repo    OrderRepository
    logger  Logger
    timeout time.Duration
}

type Option func(*OrderService)

func WithLogger(l Logger) Option {
    return func(s *OrderService) { s.logger = l }
}

func WithTimeout(d time.Duration) Option {
    return func(s *OrderService) { s.timeout = d }
}

func NewOrderService(repo OrderRepository, opts ...Option) *OrderService {
    s := &OrderService{repo: repo, timeout: 5 * time.Second}
    for _, opt := range opts {
        opt(s)
    }
    return s
}
```

This keeps the required dependencies (repo) explicit in the constructor and optional ones configurable without breaking existing callers.

## Exercises

See the [session README](../README.md#try-it-yourself) for the full exercise list. In this Go project:

1. Run the app and trace the DI chain in `main.go`: repo -> service -> handler
2. Add a `menu` package with `MenuRepository`, `MenuService`, `MenuHandler` and expose `GET /menu`
3. Create a `PostgresOrderRepository` and swap it in `main.go` (just change one line)
4. Write a unit test for `OrderService` with a stub repository
5. Run `go run ./container/demo` and compare manual DI vs the reflection-based container

> **Solutions:** See the [`solutions/session-2/go`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-2/go) branch.
