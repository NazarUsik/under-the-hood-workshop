# Session 6: Putting It All Together - Java

> Spring Boot: all patterns combined. TracingAspect X-rays the system. Tests verify the invisible.

## Prerequisites

- Java 21+
- Maven 3.9+

## Project Structure

```
java/
├── src/main/java/coffeeshop/
│   ├── CoffeeShopApplication.java
│   ├── order/                                # DI: Session 2
│   │   ├── Order.java / OrderRepository.java / InMemoryOrderRepository.java
│   │   ├── OrderService.java                 # No logging, no tracing code
│   │   └── OrderController.java
│   ├── menu/                                 # DI: Session 2
│   │   └── (same pattern)
│   ├── kitchen/                              # Chaos swap: Session 5
│   │   ├── KitchenService.java               # Interface (swap point)
│   │   ├── RealKitchenService.java           # @Profile("!chaos")
│   │   └── ChaosKitchenService.java          # @Profile("chaos")
│   ├── resilience/                           # Resilience: Session 5
│   │   ├── TimeoutKitchenService.java
│   │   ├── RetryKitchenService.java
│   │   ├── FallbackKitchenService.java
│   │   └── ResilienceConfig.java             # Fallback(Retry(Timeout(chaos)))
│   ├── aspect/                               # AOP: Session 4
│   │   ├── LoggingAspect.java                # @Order(1) - outermost
│   │   └── TimingAspect.java                 # @Order(2) - inside logging
│   └── tracing/                              # NEW: Session 6
│       ├── TraceCollector.java               # Thread-safe step recorder
│       └── TracingAspect.java                # @Order(0) - captures everything
├── src/test/java/coffeeshop/tracing/         # Trace-based tests
│   ├── TracingTestConfig.java                # Registers TraceCollector + TracingAspect
│   ├── ProxyChainOrderTest.java              # Verifies aspect ordering
│   ├── ChaosResilienceTraceTest.java         # Verifies resilience under chaos
│   └── EndToEndTraceTest.java                # Full HTTP request trace
└── src/main/resources/
    └── application.properties
```

## How to Run

**Normal mode** (everything works, logging + timing in console):

```bash
mvn spring-boot:run
```

**Chaos mode** (random failures, resilience kicks in):

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=chaos
```

**Run tests** (the main event - trace-based verification):

```bash
mvn test
```

## What's New in Session 6

The production code is identical to Sessions 4+5 combined. **Zero lines added to any service class.**

The new pieces are test-only:

- **`tracing/TraceCollector.java`** - a thread-safe list that records steps. Not a Spring bean in production.
- **`tracing/TracingAspect.java`** - `@Order(0)` aspect that intercepts all service calls and writes to the collector. Not registered in production.
- **`TracingTestConfig.java`** - `@TestConfiguration` that creates the collector and aspect beans. Only active in tests.

## What to Look At

- **`tracing/TracingAspect.java`** - one class that X-rays the entire system. `@Order(0)` means it runs outside LoggingAspect and TimingAspect.
- **`ProxyChainOrderTest.java`** - proves aspects fire in the right order. If someone swaps `@Order(1)` and `@Order(2)`, this test fails.
- **`ChaosResilienceTraceTest.java`** - runs 20 requests under chaos and verifies the trace shows retry and fallback activation.
- **`EndToEndTraceTest.java`** - sends an HTTP request via MockMvc, verifies the trace contains the full path from controller to kitchen.

## Exercises

See the [session README](../README.md#try-it-yourself) for the full exercise list. In this Java project:

1. Run `mvn test`. All three test classes pass. Read the trace output.
2. Swap `@Order(1)` and `@Order(2)` on LoggingAspect and TimingAspect. Run tests. Which test breaks?
3. Remove the FallbackKitchenService from the chain. Run `ChaosResilienceTraceTest`. What happens?
4. Add a rate limiter aspect. Write a test that verifies it fires before the service call.

> **Solutions:** See the [`solutions/session-6/java`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-6/java) branch.
