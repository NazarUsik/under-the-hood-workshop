# Session 4: Proxy Pattern and AOP - Go

> Go: Wrapper structs, interface-based proxies, composition over inheritance, no magic

## Prerequisites

- Go 1.22+

## Project Structure

```
go/
├── main.go                     # Entry point: builds proxy chain, wires handlers
├── order/
│   ├── model.go                # Order struct
│   ├── repository.go           # Interface + InMemory implementation
│   ├── service.go              # OrderService interface + real implementation
│   └── handler.go              # Gin handlers (talks to OrderService interface)
├── menu/
│   ├── model.go                # MenuItem struct
│   ├── repository.go           # Interface + InMemory implementation
│   ├── service.go              # MenuService interface + real implementation
│   └── handler.go              # Gin handler
├── proxy/
│   ├── logging.go              # LoggingOrderService, LoggingMenuService
│   └── timing.go               # TimingOrderService, TimingMenuService
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

Watch the console: `[Logging]` and `[Timing]` messages appear even though the service code has zero logging.

## What to Look At

- **`order/service.go`** - defines the `OrderService` interface AND the real implementation. The real service has no logging or timing code.
- **`proxy/logging.go`** - `LoggingOrderService` wraps `OrderService`. It has the same interface. The handler can't tell the difference. This IS the Proxy pattern.
- **`proxy/timing.go`** - `TimingOrderService` wraps `OrderService` with timing. Stacks with logging.
- **`main.go` lines 22-28** - builds the proxy chain explicitly: `Logging(Timing(real))`. No annotations, no reflection. Just Go composition.

## Go Proxies: The Explicit Version

In Go, the Proxy pattern is completely transparent. No bytecode generation, no reflection, no annotations. Just interfaces and struct composition:

```go
// The interface (same for proxy and real)
type OrderService interface {
    ListOrders() []Order
    FindOrder(id int) *Order
}

// The real service
type orderServiceImpl struct { repo OrderRepository }
func (s *orderServiceImpl) ListOrders() []Order { return s.repo.FindAll() }

// The proxy: same interface, wraps the real service
type LoggingOrderService struct { Inner OrderService }
func (p *LoggingOrderService) ListOrders() []Order {
    fmt.Println("[Logging] >> ListOrders")
    result := p.Inner.ListOrders()  // delegate to real (or next proxy)
    fmt.Println("[Logging] << ListOrders")
    return result
}
```

### Building the Proxy Chain

```go
real := NewOrderService(repo)
timed := &TimingOrderService{Inner: real}
logged := &LoggingOrderService{Inner: timed}
handler := NewHandler(logged)  // handler sees OrderService interface
```

Call flow: `Handler -> Logging.before -> Timing.before -> real -> Timing.after -> Logging.after`

The handler doesn't know or care that `logged` is a proxy. It just calls `service.ListOrders()`.

### Why Go's Approach Is Different

| Feature              | Spring/NestJS/Python                | Go                                    |
|----------------------|-------------------------------------|---------------------------------------|
| Proxy creation       | Framework magic (CGLIB, decorators) | Manual struct composition             |
| Self-invocation trap | Yes (proxy bypassed)                | No (each call goes through `p.Inner`) |
| Final method problem | Yes (CGLIB can't override final)    | No (interfaces, not subclasses)       |
| Runtime cost         | Reflection/bytecode overhead        | Zero overhead (direct function calls) |
| Boilerplate          | Low (annotations)                   | Higher (one wrapper per method)       |
| Transparency         | Hidden (you don't see the proxy)    | Explicit (you build the chain)        |

Go trades convenience for clarity. You write more code, but you always know exactly what's happening.

### Reducing Boilerplate: Generic Proxy

For many methods, you can reduce boilerplate with a generic wrapper:

```go
func withLogging[T any](name string, fn func() T) T {
    fmt.Printf("[Logging] >> %s\n", name)
    result := fn()
    fmt.Printf("[Logging] << %s\n", name)
    return result
}

func (p *LoggingOrderService) ListOrders() []Order {
    return withLogging("ListOrders", p.Inner.ListOrders)
}
```

## Exercises

See the [session README](../README.md#try-it-yourself) for the full exercise list. In this Go project:

1. Run the app, hit `GET /orders`, and read the console output. Trace: Logging -> Timing -> real -> Timing -> Logging.
2. Create a `CachingMenuService` proxy that caches the result of `ListItems()`.
3. Create a `ValidatingOrderService` proxy that checks `id > 0` before calling `FindOrder()`.
4. Stack three proxies: `Logging(Timing(Caching(real)))` on `MenuService`. Verify order in logs.
5. Create a generic `withTiming` helper function and use it to reduce boilerplate in `TimingOrderService`.

> **Solutions:** See the [`solutions/session-4/go`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-4/go) branch.
