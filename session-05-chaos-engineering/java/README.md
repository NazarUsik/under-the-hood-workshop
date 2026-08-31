# Session 5: Chaos Engineering - Java

> Spring Boot: `@Profile`-based service swapping, chaos injection, resilience proxies (timeout + fallback)

## Prerequisites

- Java 21+
- Maven 3.9+

## Project Structure

```
java/
├── src/main/java/coffeeshop/
│   ├── CoffeeShopApplication.java
│   ├── order/
│   │   ├── Order.java
│   │   ├── OrderRepository.java / InMemoryOrderRepository.java
│   │   ├── OrderService.java            # Calls KitchenService.prepare()
│   │   └── OrderController.java         # POST /orders/{id}/prepare triggers chaos
│   ├── menu/
│   │   ├── MenuItem.java
│   │   ├── MenuRepository.java / InMemoryMenuRepository.java
│   │   ├── MenuService.java
│   │   └── MenuController.java
│   ├── kitchen/
│   │   ├── Preparation.java
│   │   ├── KitchenService.java          # Interface (the swap point)
│   │   ├── RealKitchenService.java      # @Profile("!chaos") - always works
│   │   └── ChaosKitchenService.java     # @Profile("chaos") - randomly fails
│   └── resilience/
│       ├── TimeoutKitchenService.java   # Proxy: timeout after 3s
│       ├── FallbackKitchenService.java  # Proxy: returns "queued" on failure
│       └── ResilienceConfig.java        # Wires Fallback(Timeout(Chaos))
└── src/main/resources/
    └── application.properties           # Uncomment to enable chaos
```

## How to Run

**Normal mode** (everything works):

```bash
mvn spring-boot:run
```

**Chaos mode** (random failures):

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=chaos
```

Server starts on http://localhost:8080.

- `GET /orders` - list all orders
- `GET /orders/1` - get order by ID
- `POST /orders/1/prepare` - prepare an order (this is where chaos happens)
- `GET /menu` - list menu items

## How the DI Swap Works

Spring's `@Profile` annotation controls which bean gets created:

```java

@Service
@Profile("!chaos")    // active when chaos is NOT enabled
public class RealKitchenService implements KitchenService { ...
}

@Service
@Profile("chaos")     // active ONLY when chaos is enabled
public class ChaosKitchenService implements KitchenService { ...
}
```

`OrderService` depends on the `KitchenService` interface. It doesn't know or care which implementation it gets. Spring picks based on the active profile.

## What to Look At

- **`kitchen/KitchenService.java`** - the interface. This is the swap point. Both real and chaos implement it.
- **`kitchen/ChaosKitchenService.java`** - injects three types of failure: crashes (30%), latency (20%), bad data (10%), success (40%).
- **`resilience/ResilienceConfig.java`** - wraps the chaos service with `Fallback(Timeout(chaos))`. This is the same Proxy pattern from Session 4, now used for resilience.
- **`resilience/TimeoutKitchenService.java`** - cancels the call if it takes longer than 3 seconds.
- **`resilience/FallbackKitchenService.java`** - catches any exception and returns a "queued" status instead.

## Chaos Flow

```
POST /orders/1/prepare

Normal:    OrderService -> RealKitchenService -> Preparation("preparing")

Chaos:     OrderService -> FallbackKitchenService
                              -> TimeoutKitchenService (3s max)
                                    -> ChaosKitchenService
                                          -> 30% crash -> caught by Fallback -> "queued"
                                          -> 20% latency -> caught by Timeout -> "queued"
                                          -> 10% bad data -> Preparation("UNKNOWN_STATUS")
                                          -> 40% success -> Preparation("preparing")
```

## Exercises

See the [session README](../README.md#try-it-yourself) for the full exercise list. In this Java project:

1. Run in normal mode. `POST /orders/1/prepare` always returns `"preparing"`.
2. Run in chaos mode. Hit `POST /orders/1/prepare` 20 times. Count successes, failures, and "queued" fallbacks.
3. Add a `RetryKitchenService` proxy that retries up to 3 times on failure before giving up.
4. Add a simple `CircuitBreakerKitchenService` that opens after 3 consecutive failures and stays open for 10 seconds.
5. Stack them: `Fallback(Retry(CircuitBreaker(Timeout(chaos))))`. Hit the endpoint 50 times and observe the circuit breaker tripping.

> **Solutions:** See the [`solutions/session-5/java`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-5/java) branch.
