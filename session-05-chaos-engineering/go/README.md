# Session 5: Chaos Engineering - Go

> Go: Interface-based service swapping, chaos injection via env var, resilience wrappers with `context.WithTimeout`

## Prerequisites

- Go 1.22+

## Project Structure

```
go/
├── main.go                     # Entry point: builds normal or chaos service chain
├── order/
│   ├── model.go                # Order struct
│   ├── repository.go           # Interface + InMemory implementation
│   ├── service.go              # Calls KitchenService.Prepare()
│   └── handler.go              # Gin handlers (POST /orders/:id/prepare)
├── menu/
│   ├── model.go / repository.go / service.go
│   └── handler.go
├── kitchen/
│   └── service.go              # Interface + Real + Chaos implementations
├── resilience/
│   ├── timeout.go              # TimeoutKitchenService (context.WithTimeout)
│   └── fallback.go             # FallbackKitchenService (returns "queued")
└── go.mod
```

## How to Run

**Normal mode** (everything works):

```bash
go run .
```

**Chaos mode** (random failures):

```bash
CHAOS_MODE=true go run .
```

Server starts on http://localhost:8080.

- `GET /orders` - list all orders
- `GET /orders/1` - get order by ID
- `POST /orders/1/prepare` - prepare an order (chaos happens here)
- `GET /menu` - list menu items

## How the DI Swap Works

Go's approach is fully explicit. No annotations, no profiles. Just an `if` in `main()`:

```go
var kitchenService kitchen.KitchenService
if chaosMode {
    chaos := &kitchen.ChaosKitchenService{}
    withTimeout := &resilience.TimeoutKitchenService{Inner: chaos, Timeout: 3 * time.Second}
    kitchenService = &resilience.FallbackKitchenService{Inner: withTimeout}
} else {
    kitchenService = &kitchen.RealKitchenService{}
}
```

`OrderService` takes a `KitchenService` interface. It never knows which implementation it gets.

## What to Look At

- **`kitchen/service.go`** - `KitchenService` interface, `RealKitchenService`, `ChaosKitchenService`. The chaos version returns errors (not panics) because Go uses explicit error
  handling.
- **`resilience/timeout.go`** - uses `context.WithTimeout` and a goroutine. If the inner service doesn't respond in time, the context cancels and an error is returned.
- **`resilience/fallback.go`** - checks `if err != nil` and returns a fallback `Preparation`. This is Go's version of `try/catch`.
- **`main.go`** - the swap point. `CHAOS_MODE` env var controls which chain gets built.

## Go's Error Handling vs Exceptions

In Go, chaos injection returns errors instead of throwing exceptions:

```go
func (s *ChaosKitchenService) Prepare(orderID int, drink string) (*Preparation, error) {
    if roll < 0.3 {
        return nil, fmt.Errorf("kitchen equipment malfunction!")  // error, not panic
    }
    return &Preparation{...}, nil
}
```

The caller checks `if err != nil` instead of wrapping in `try/catch`. The resilience wrappers follow the same pattern:

```go
func (s *FallbackKitchenService) Prepare(orderID int, drink string) (*Preparation, error) {
    prep, err := s.Inner.Prepare(orderID, drink)
    if err != nil {
        return &Preparation{Status: "queued"}, nil  // fallback
    }
    return prep, nil
}
```

## Exercises

See the [session README](../README.md#try-it-yourself) for the full exercise list. In this Go project:

1. Run in normal mode. `POST /orders/1/prepare` always returns `"preparing"`.
2. Run in chaos mode. Hit `POST /orders/1/prepare` 20 times. Count successes, failures, and "queued" fallbacks.
3. Add a `RetryKitchenService` that retries up to 3 times on error before giving up.
4. Add a `CircuitBreakerKitchenService` that opens after 3 consecutive errors and stays open for 10 seconds.
5. Stack them: `Fallback(Retry(CircuitBreaker(Timeout(chaos))))`. Hit the endpoint 50 times.

> **Solutions:** See the [`solutions/session-5/go`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-5/go) branch.
