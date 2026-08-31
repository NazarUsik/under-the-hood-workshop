# Session 6: Putting It All Together - Go

> Go: all patterns combined. `TracedKitchenService` wrapper X-rays the system. `go test` verifies the invisible.

## Prerequisites

- Go 1.22+

## Project Structure

```
go/
├── main.go                          # Entry point: chaos/normal toggle
├── order/ menu/                     # DI: Session 2
├── kitchen/
│   └── service.go                   # Interface + Real + Chaos
├── resilience/                      # Session 5
│   ├── timeout.go / retry.go / fallback.go
├── tracing/                         # NEW: Session 6
│   ├── collector.go                 # TraceCollector (thread-safe step recorder)
│   ├── traced_kitchen.go            # TracedKitchenService: wraps any KitchenService
│   └── trace_test.go               # 3 trace-based tests
└── go.mod
```

## How to Run

```bash
go run .                             # normal mode
CHAOS_MODE=true go run .             # chaos mode
go test ./tracing/ -v                # trace-based tests
```

## What's New in Session 6

- **`tracing/collector.go`** - `TraceCollector`: mutex-protected step list.
- **`tracing/traced_kitchen.go`** - `TracedKitchenService`: wraps any `KitchenService` and records steps. Go's version of AOP: struct composition around an interface.
- **`tracing/trace_test.go`** - 3 tests: normal trace, chaos resilience trace, stress with 20 requests.

## Go's Approach to "AOP"

Go has no annotations, no proxies, no aspects. Instead, you wrap interfaces with structs:

```go
type TracedKitchenService struct {
    Inner     kitchen.KitchenService
    Collector *TraceCollector
    Label     string
}

func (s *TracedKitchenService) Prepare(orderID int, drink string) (*kitchen.Preparation, error) {
    s.Collector.Add("before:" + s.Label + ".Prepare")
    prep, err := s.Inner.Prepare(orderID, drink)
    // ...
}
```

Same result as Java's `@Aspect` or Python's decorator: the inner service has zero knowledge of tracing.

## Exercises

1. Run `go test ./tracing/ -v`. Read the trace output.
2. Remove `FallbackKitchenService` from the chain. Which test fails?
3. Add a `RateLimiterKitchenService` wrapper. Write a test verifying it rejects after 10 calls.

> **Solutions:** See the [`solutions/session-6/go`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-6/go) branch.
